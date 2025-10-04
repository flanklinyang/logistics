package com.yang.service.impl;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.yang.dto.LogisticsOrderDTO;
import com.yang.dto.LogisticsPackageDTO;
import com.yang.dto.LogisticsProductDTO;
import com.yang.dto.PrintPdfsDTO;
import com.yang.entity.LogisticsOrder;
import com.yang.entity.LogisticsPackage;
import com.yang.entity.LogisticsProduct;
import com.yang.exception.UserSignNullException;
import com.yang.mapper.LogisticsOrderMapper;
import com.yang.service.LogisticsOrderService;
import com.yang.vo.LogisticsOrderVO;
import com.yang.vo.LogisticsPackageVO;
import com.yang.vo.LogisticsProductVO;
import com.yang.exception.OrderNumberDuplicateKeyException;
import com.yang.vo.PrintPdfsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class LogisticsOrderServiceImpl implements LogisticsOrderService {

    @Autowired
    private LogisticsOrderMapper logisticsOrderMapper;

    /**
     * 新增物流订单
     * @param logisticsOrderDTO 物流订单DTO
     * @return 新增成功后的物流订单VO
     */
    @Override
    public LogisticsOrderVO addLogisticsOrder(LogisticsOrderDTO logisticsOrderDTO) throws IOException {

        Integer count = logisticsOrderMapper.countByOrderNumber(logisticsOrderDTO.getOrderNumber());
        if (count != null && count > 0) {
            throw new OrderNumberDuplicateKeyException(logisticsOrderDTO.getOrderNumber());
        }

        LogisticsOrder logisticsOrder = new LogisticsOrder();
        BeanUtils.copyProperties(logisticsOrderDTO,logisticsOrder);
        Random random = new Random();
        int freight = random.nextInt(10, 100);
        logisticsOrder.setFreight(BigDecimal.valueOf(freight));
        logisticsOrder.setOrderStatus("派送中");
        logisticsOrder.setPassengerOrder(System.currentTimeMillis() + UUID.randomUUID().toString());

        int totalQuantity = 0;
        double totalHeavy = 0.0;
        for (LogisticsPackageDTO packageDTO : logisticsOrderDTO.getPackageList()) {
            for (LogisticsProductDTO productDTO : packageDTO.getSpecSubList()) {
                totalQuantity += productDTO.getNumber();
                totalHeavy += productDTO.getNumber() * Double.parseDouble(packageDTO.getPackageHeavy());
            }
        }
        logisticsOrder.setQuantityWeight(totalQuantity + "件/" + totalHeavy + "kg");

        BigDecimal totalWeight = BigDecimal.ZERO;
        for (LogisticsPackageDTO packageDTO : logisticsOrderDTO.getPackageList()) {
            totalWeight = totalWeight.add(BigDecimal.valueOf(Double.parseDouble(packageDTO.getPackageHeavy())));
        }
        logisticsOrder.setWeight(totalWeight);

        String no=generateTransferNo();
        log.info("新增物流订单，订单号：{}",no);
        logisticsOrder.setTransferNo(no);

        logisticsOrder.setMerchantNumber(System.currentTimeMillis() + UUID.randomUUID().toString());

        String pdfUrl ="D:/JavaProject/logistics/server/src/main/resources/PDF/" + logisticsOrder.getTransferNo() + ".pdf";
        logisticsOrder.setPdfUrl(pdfUrl);
        Date currentTime = new Date();
        logisticsOrder.setCreateTime(currentTime);
        logisticsOrder.setUpdateTime(currentTime);

        logisticsOrderMapper.insertLogisticsOrder(logisticsOrder);
        LogisticsOrderVO logisticsOrderVO = new LogisticsOrderVO();
        BeanUtils.copyProperties(logisticsOrder,logisticsOrderVO);
        logisticsOrderVO.setFreight(logisticsOrder.getFreight().doubleValue());
        logisticsOrderVO.setWeight(logisticsOrder.getWeight().doubleValue());
        List<LogisticsPackageVO> logisticsPackageVOList = new ArrayList<>();
        for (LogisticsPackageDTO packageDTO : logisticsOrderDTO.getPackageList()) {

            LogisticsPackage logisticsPackage = new LogisticsPackage();
            BeanUtils.copyProperties(packageDTO,logisticsPackage);
            logisticsPackage.setOrderId(logisticsOrder.getId());
            logisticsOrderMapper.insertLogisticsPackage(logisticsPackage);

            LogisticsPackageVO logisticsPackageVO = new LogisticsPackageVO();
            BeanUtils.copyProperties(logisticsPackage,logisticsPackageVO);
            logisticsPackageVO.setPackageHeavy(Double.parseDouble(logisticsPackage.getPackageHeavy()));
            logisticsPackageVO.setPackageWidth(Double.parseDouble(logisticsPackage.getPackageWidth()));
            logisticsPackageVO.setPackageHigh(Double.parseDouble(logisticsPackage.getPackageHigh()));
            logisticsPackageVO.setPackageLength(Double.parseDouble(logisticsPackage.getPackageLength()));
            logisticsPackageVOList.add(logisticsPackageVO);

            List<LogisticsProductVO> logisticsProductVOList = new ArrayList<>();

            for (LogisticsProductDTO logisticsProductDTO : packageDTO.getSpecSubList()) {

                LogisticsProduct logisticsProduct = new LogisticsProduct();
                BeanUtils.copyProperties(logisticsProductDTO,logisticsProduct);
                logisticsProduct.setPackageId(logisticsPackage.getId());
                logisticsOrderMapper.insertLogisticsProduct(logisticsProduct);

                LogisticsProductVO logisticsProductVO = new LogisticsProductVO();
                BeanUtils.copyProperties(logisticsProduct,logisticsProductVO);
                logisticsProductVO.setPrice(logisticsProduct.getPrice().doubleValue());
                logisticsProductVO.setMoney(logisticsProduct.getMoney().doubleValue());
                logisticsProductVOList.add(logisticsProductVO);
            }
            logisticsPackageVO.setLogisticsProductVOList(logisticsProductVOList);
        }
        logisticsOrderVO.setPackageList(logisticsPackageVOList);
        generatePdf(logisticsOrderVO);
        return logisticsOrderVO;
    }

    /**
     * 查询物流订单详情
     * @param printPdfsDTO 批量打印物流订单DTO
     * @return 物流订单详情
     */
    @Override
    public PrintPdfsVO printPdf(PrintPdfsDTO printPdfsDTO) {
        return null;
    }

    /**
     * 生成物流订单号
     * @return 物流订单号
     */
    private String generateTransferNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString().split("-")[0];
    }

    /**
     * 生成pdf
     * @param logisticsOrderVO 物流单VO
     */
    private void generatePdf(LogisticsOrderVO logisticsOrderVO) throws IOException {
        String pdfPath = "D:/JavaProject/logistics/server/src/main/resources/PDF/" + logisticsOrderVO.getTransferNo() + ".pdf";
        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // 中文字体设置
        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        document.setFont(font);

        document.add(new Paragraph("订单基本信息").setFontSize(14).setBold().setMarginBottom(10));
        Table baseTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}));
        baseTable.setWidth(UnitValue.createPercentValue(100));

        addKeyValueCell(baseTable, "物流单号", logisticsOrderVO.getTransferNo(), font);
        addKeyValueCell(baseTable, "运费", String.valueOf(logisticsOrderVO.getFreight()), font);
        addKeyValueCell(baseTable, "订单状态", logisticsOrderVO.getOrderStatus(), font);
        addKeyValueCell(baseTable, "系统内部订单号", logisticsOrderVO.getPassengerOrder(), font);
        addKeyValueCell(baseTable, "件数/重量", logisticsOrderVO.getQuantityWeight(), font);
        addKeyValueCell(baseTable, "计算重量", String.valueOf(logisticsOrderVO.getWeight()), font);
        addKeyValueCell(baseTable, "商户编号", logisticsOrderVO.getMerchantNumber(), font);
        addKeyValueCell(baseTable, "商户号", logisticsOrderVO.getUserSign(), font);
        addKeyValueCell(baseTable, "商户订单号", logisticsOrderVO.getOrderNumber(), font);
        addKeyValueCell(baseTable, "下单产品", logisticsOrderVO.getOrderingProducts(), font);
        addKeyValueCell(baseTable, "发件地", logisticsOrderVO.getFromPlace(), font);
        addKeyValueCell(baseTable, "目的地", logisticsOrderVO.getDestination(), font);
        addKeyValueCell(baseTable, "创建时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logisticsOrderVO.getCreateTime()), font);
        addKeyValueCell(baseTable, "更新时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(logisticsOrderVO.getUpdateTime()), font);
        document.add(baseTable);

        document.add(new Paragraph("\n发件人信息").setFontSize(14).setBold().setMarginTop(15).setMarginBottom(10));
        Table senderTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        senderTable.setWidth(UnitValue.createPercentValue(100));
        addKeyValueCell(senderTable, "发件人姓名", logisticsOrderVO.getSenderName(), font);
        addKeyValueCell(senderTable, "发件公司", logisticsOrderVO.getSenderCompany(), font);
        addKeyValueCell(senderTable, "发件地址", logisticsOrderVO.getSenderAddress(), font);
        addKeyValueCell(senderTable, "发件地址补充", logisticsOrderVO.getSenderAddressOne(), font);
        addKeyValueCell(senderTable, "发件省/州", logisticsOrderVO.getSenderProvince(), font);
        addKeyValueCell(senderTable, "发件城市", logisticsOrderVO.getSenderCity(), font);
        addKeyValueCell(senderTable, "发件邮编", logisticsOrderVO.getSenderPostalCode(), font);
        addKeyValueCell(senderTable, "发件街道/门牌", logisticsOrderVO.getSenderStreet(), font);
        addKeyValueCell(senderTable, "发件手机", logisticsOrderVO.getSendMobilePhone(), font);
        addKeyValueCell(senderTable, "发件电话", logisticsOrderVO.getSendCall(), font);
        document.add(senderTable);

        document.add(new Paragraph("\n收件人信息").setFontSize(14).setBold().setMarginTop(15).setMarginBottom(10));
        Table recipientTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        recipientTable.setWidth(UnitValue.createPercentValue(100));
        addKeyValueCell(recipientTable, "收件人姓名", logisticsOrderVO.getRecipientName(), font);
        addKeyValueCell(recipientTable, "收件公司", logisticsOrderVO.getRecipientCompany(), font);
        addKeyValueCell(recipientTable, "收件地址", logisticsOrderVO.getRecipientAddress(), font);
        addKeyValueCell(recipientTable, "收件地址补充", logisticsOrderVO.getRecipientAddressOne(), font);
        addKeyValueCell(recipientTable, "收件省/州", logisticsOrderVO.getRecipientProvince(), font);
        addKeyValueCell(recipientTable, "收件城市", logisticsOrderVO.getRecipientCity(), font);
        addKeyValueCell(recipientTable, "收件邮编", logisticsOrderVO.getRecipientPostalCode(), font);
        addKeyValueCell(recipientTable, "收件街道/门牌", logisticsOrderVO.getRecipientStreet(), font);
        addKeyValueCell(recipientTable, "收件手机", logisticsOrderVO.getRecipientMobilePhone(), font);
        addKeyValueCell(recipientTable, "收件电话", logisticsOrderVO.getRecipientPhone(), font);
        document.add(recipientTable);

        document.add(new Paragraph("\n包裹及商品信息").setFontSize(14).setBold().setMarginTop(15).setMarginBottom(10));
        int packageIndex = 1;
        for (LogisticsPackageVO packageVO : logisticsOrderVO.getPackageList()) {

            document.add(new Paragraph("包裹 " + packageIndex).setFontSize(12).setBold().setMarginTop(10));
            Table packageTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            packageTable.setWidth(UnitValue.createPercentValue(100));
            addKeyValueCell(packageTable, "包裹说明", packageVO.getPackageExplain(), font);
            addKeyValueCell(packageTable, "包裹尺寸",
                    String.format("长:%.1f, 宽:%.1f, 高:%.1f",
                            packageVO.getPackageLength(),
                            packageVO.getPackageWidth(),
                            packageVO.getPackageHigh()), font);
            addKeyValueCell(packageTable, "包裹重量", String.valueOf(packageVO.getPackageHeavy()), font);
            document.add(packageTable);


            document.add(new Paragraph("包裹 " + packageIndex + " 包含商品").setFontSize(11).setBold().setMarginTop(5));
            Table productTable = new Table(UnitValue.createPercentArray(new float[]{15, 15, 10, 10, 15, 15, 20}));
            productTable.setWidth(UnitValue.createPercentValue(100));

            productTable.addHeaderCell(new Cell().add(new Paragraph("中文品名").setFontSize(10)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("英文品名").setFontSize(10)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("币种").setFontSize(10)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("数量").setFontSize(10)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("销售金额").setFontSize(10)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("申报价格").setFontSize(10)));
            productTable.addHeaderCell(new Cell().add(new Paragraph("规格").setFontSize(10)));

            for (LogisticsProductVO productVO : packageVO.getLogisticsProductVOList()) {
                productTable.addCell(new Cell().add(new Paragraph(productVO.getChineseName()).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(productVO.getEnglishName()).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(productVO.getSpecie()).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(String.valueOf(productVO.getNumber())).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(String.valueOf(productVO.getMoney())).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(String.valueOf(productVO.getPrice())).setFontSize(10)));
                productTable.addCell(new Cell().add(new Paragraph(productVO.getSpecifications()).setFontSize(10)));
            }
            document.add(productTable);
            packageIndex++;
        }

        document.add(new Paragraph("\n物流单号条形码").setFontSize(12).setBold().setMarginTop(20).setMarginBottom(10));
        Barcode128 barcode = new Barcode128(pdfDoc);
        barcode.setCode(logisticsOrderVO.getTransferNo());
        barcode.setSize(5);
        barcode.setCodeType(Barcode128.CODE128);
        barcode.setBaseline(10);
        barcode.setBarHeight(25);
        PdfFormXObject xObject = barcode.createFormXObject(new DeviceRgb(0, 0, 0), null, pdfDoc);
        Image image = new Image(xObject);
        image.setWidth(UnitValue.createPercentValue(50));
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(image);

        document.close();
        pdfDoc.close();
        writer.close();
    }

    private void addKeyValueCell(Table table, String key, String value, PdfFont font) {
        table.addCell(new Cell().add(new Paragraph(key).setFontSize(10).setFont(font)));
        table.addCell(new Cell().add(new Paragraph(value == null ? "" : value).setFontSize(10).setFont(font)));
    }

    private void generatePdfByTransferNos(List<LogisticsOrderVO> logisticsOrderVOList) throws IOException {

    }
}

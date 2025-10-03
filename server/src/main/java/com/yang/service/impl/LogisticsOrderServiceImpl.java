package com.yang.service.impl;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.kernel.colors.Color;
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
import com.itextpdf.layout.properties.UnitValue;
import com.yang.dto.LogisticsOrderDTO;
import com.yang.dto.LogisticsPackageDTO;
import com.yang.dto.LogisticsProductDTO;
import com.yang.entity.LogisticsOrder;
import com.yang.entity.LogisticsPackage;
import com.yang.entity.LogisticsProduct;
import com.yang.mapper.LogisticsOrderMapper;
import com.yang.service.LogisticsOrderService;
import com.yang.vo.LogisticsOrderVO;
import com.yang.vo.LogisticsPackageVO;
import com.yang.vo.LogisticsProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
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
        LogisticsOrder logisticsOrder = new LogisticsOrder();
        BeanUtils.copyProperties(logisticsOrderDTO,logisticsOrder);

        logisticsOrder.setFreight(BigDecimal.valueOf(10));
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
     * 生成物流订单号
     * @return 物流订单号
     */
    private String generateTransferNo() {
        return System.currentTimeMillis() + UUID.randomUUID().toString();
    }

    private void generatePdf(LogisticsOrderVO logisticsOrderVO) throws IOException {

        String pdfPath = "D:/JavaProject/logistics/server/src/main/resources/PDF/" + logisticsOrderVO.getTransferNo() + ".pdf"; // transferNo为物流单号，从接口响应中获取
        PdfWriter writer = new PdfWriter(pdfPath);

        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        document.setFont(font);

        document.add(new Paragraph("订单主要信息").setFontSize(12).setBold());

        Table Ordertable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30}));
        Ordertable.setWidth(UnitValue.createPercentValue(100));

        Ordertable.addCell(new Cell().add(new Paragraph("商户号")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getMerchantNumber())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("物流单号")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getTransferNo())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("发件人")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getSenderName())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("发件地址")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getSenderAddress())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("收件地址")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getRecipientAddress())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("收件人")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getRecipientName())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("发件人电话")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getSendMobilePhone())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("收件人电话")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getRecipientPhone())).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("运费")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsOrderVO.getFreight()))).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph("订单状态")).setFontSize(10));
        Ordertable.addCell(new Cell().add(new Paragraph(logisticsOrderVO.getOrderStatus())).setFontSize(10));
        document.add(Ordertable);

        document.add(new Paragraph("包裹信息").setFontSize(12).setBold());
        for(int i=0;i<logisticsOrderVO.getPackageList().size();i++){
            LogisticsPackageVO logisticsPackageVO = logisticsOrderVO.getPackageList().get(i);
            document.add(new Paragraph("包裹"+(i+1)).setFontSize(10).setBold());
            Table packagetable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30}));
            packagetable.setWidth(UnitValue.createPercentValue(100));
            packagetable.addCell(new Cell().add(new Paragraph("包裹描述")).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph(logisticsPackageVO.getPackageExplain())).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph("包裹重量")).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getPackageHeavy()))).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph("包裹宽度")).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getPackageWidth()))).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph("包裹高度")).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getPackageHigh()))).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph("包裹长度")).setFontSize(10));
            packagetable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getPackageLength()))).setFontSize(10));

            document.add(packagetable);
            document.add(new Paragraph("包裹"+(i+1)+"商品信息").setFontSize(10).setBold());
            for(int j=0;j<logisticsPackageVO.getLogisticsProductVOList().size();j++){
                document.add(new Paragraph("商品"+(j+1)).setFontSize(10).setBold());
                Table producttable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30}));
                producttable.setWidth(UnitValue.createPercentValue(100));
                producttable.addCell(new Cell().add(new Paragraph("中文名称")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(logisticsPackageVO.getLogisticsProductVOList().get(j).getChineseName())).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("英文名称")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(logisticsPackageVO.getLogisticsProductVOList().get(j).getEnglishName())).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("商品种类")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(logisticsPackageVO.getLogisticsProductVOList().get(j).getSpecie())).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("商品数量")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getLogisticsProductVOList().get(j).getNumber()))).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("商品单价")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getLogisticsProductVOList().get(j).getPrice()))).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("商品金额")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(String.valueOf(logisticsPackageVO.getLogisticsProductVOList().get(j).getMoney()))).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("商品单位")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(logisticsPackageVO.getLogisticsProductVOList().get(j).getCurrency())).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph("商品规格")).setFontSize(10));
                producttable.addCell(new Cell().add(new Paragraph(logisticsPackageVO.getLogisticsProductVOList().get(j).getSpecifications())).setFontSize(10));
                document.add(producttable);
            }
        }

        Barcode128 barcode = new Barcode128(pdfDoc);
        barcode.setCode(logisticsOrderVO.getTransferNo());
        barcode.setSize(12);
        barcode.setCodeType(Barcode128.CODE128);
        Color black = new DeviceRgb(0, 0, 0);
        PdfFormXObject xObject = barcode.createFormXObject(black, null, pdfDoc);
        Image image = new Image(xObject);
        image.setWidth(UnitValue.createPercentValue(50));
        document.add(new Paragraph("物流单号条形码").setFontSize(10).setMarginTop(10));
        document.add(image.setMarginTop(5));

        document.close();
        pdfDoc.close();
        writer.close();
    }
}

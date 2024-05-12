import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChangeOnFileName {
    public static final String DEST = "Out/OnNameChange.pdf"; // Output path
    public static final String HTML1 = "1.html"; // Layer for good.pdf
    public static final String HTML2 = "3.html"; // Layer for bad.pdf
    public static final String HTML3 = "3.html"; // Layer for other names

    public static void main(String[] args) {
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
            pdfDoc.addNewPage();

            // Create layers (OCGs) for HTML content
            PdfLayer layer1 = new PdfLayer("GoodLayer", pdfDoc);
            layer1.setOn(false); // Initially not visible
            PdfLayer layer2 = new PdfLayer("BadLayer", pdfDoc);
            layer2.setOn(false); // Initially not visible
            PdfLayer layer3 = new PdfLayer("OtherLayer", pdfDoc);
            layer3.setOn(false); // Default visible layer
// Define font
PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
float fontSize = 18;
      // Support Layer (Visible by default)
      PdfLayer supportLayer = new PdfLayer("SupportLayer", pdfDoc);
      PdfCanvas supportCanvas = new PdfCanvas(pdfDoc.getLastPage());
      supportCanvas.beginLayer(supportLayer);
      supportCanvas.setFontAndSize(font, 12);
      supportCanvas.beginText().moveText(100, 440).showText("This PDF file may not be compatible with your current viewer.").endText();
      supportCanvas.beginText().moveText(100, 400).showText("Please open it with Adobe Acrobat Reader for optimal viewing.").endText();
      supportCanvas.endLayer();

            // Convert HTML to PDF and draw on layers
            drawHtmlOnLayer(pdfDoc, HTML1, layer1);
            drawHtmlOnLayer(pdfDoc, HTML2, layer2);
            drawHtmlOnLayer(pdfDoc, HTML3, layer3);

            // Embed JavaScript to check file name and show appropriate layer
            String jsCode =
            
                
                "var docFileName = this.documentFileName.toLowerCase();\n" +
                
               
                "var layerGood = this.getOCGs()[0];\n" +
                "var layerBad = this.getOCGs()[1];\n" +
                "var layerOther = this.getOCGs()[2];\n" +
                "var layerSupport = this.getOCGs()[3];\n" +
                "if (layerSupport) layerSupport.state = false;\n" +
                "if (docFileName.indexOf('good.pdf') !== -1) {\n" +
                "    layerGood.state = true;\n" +
                "    layerBad.state = false;\n" +
                "    layerOther.state = false;\n" +
                "} else if (docFileName.indexOf('bad.pdf') !== -1) {\n" +
                "    layerGood.state = false;\n" +
                "    layerBad.state = true;\n" +
                "    layerOther.state = false;\n" +
                "} else {\n" +
                "    layerGood.state = false;\n" +
                "    layerBad.state = false;\n" +
                "    layerOther.state = true;\n" +
                "}";

            pdfDoc.getCatalog().setOpenAction(PdfAction.createJavaScript(jsCode));

            pdfDoc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void drawHtmlOnLayer(PdfDocument pdfDoc, String htmlFile, PdfLayer layer) throws IOException {
        String tempPdf = "temp.pdf";
        HtmlConverter.convertToPdf(new FileInputStream(htmlFile), new FileOutputStream(tempPdf));
    
        PdfDocument tempDoc = new PdfDocument(new PdfReader(tempPdf));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage().newContentStreamBefore(), pdfDoc.getLastPage().getResources(), pdfDoc);
        canvas.beginLayer(layer);
        PdfFormXObject pageCopy = tempDoc.getFirstPage().copyAsFormXObject(pdfDoc);
        canvas.addXObjectAt(pageCopy, 0, 0);
        canvas.endLayer();
    
        tempDoc.close();
        new File(tempPdf).delete();
    }
}

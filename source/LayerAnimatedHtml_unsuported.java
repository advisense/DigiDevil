// Inspiration https://acrobatusers.com/tutorials/create_use_layers/

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LayerAnimatedHtml_unsuported {
    public static final String DEST = "Out/JS_Animated_Unsuported_JS_2_Layer_Animated_Realistic.pdf"; // Output path
    public static final String HTML1 = "1.html"; // Adjust paths as needed for HTML of layer 1
    public static final String HTML2 = "3.html"; // Adjust paths as needed for HTML of layer 1

    public static void main(String[] args) {
        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
            pdfDoc.addNewPage();

            // Define font
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            float fontSize = 18;

            // Create layers (OCGs) for HTML content
            PdfLayer layer1 = new PdfLayer("LayerOne", pdfDoc);
            layer1.setOn(false); // Initially not visible
            PdfLayer layer2 = new PdfLayer("LayerTwo", pdfDoc);
            layer2.setOn(false); // Initially not visible

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

            // JavaScript to toggle layer visibility and hide the support layer if JavaScript is supported
            String jsCode = 
                "function FindOCG(name) { var ocgs = this.getOCGs(); for (var i = 0; i < ocgs.length; i++) { if (ocgs[i].name == name) return ocgs[i]; } return null; }\n" +
                "var aLayers = ['LayerOne', 'LayerTwo'];\n" +
                "var inc = -1;\n" +
                "function ToggleOCGs() { \n" +
                "   if(inc >= 0){ var ocg = FindOCG(aLayers[inc]); ocg.state = !ocg.state; }\n" +
                "   inc++; if(inc >= aLayers.length) inc = 0;\n" +
                "   var ocg = FindOCG(aLayers[inc]); ocg.state = !ocg.state;\n" +
                "}\n" +
                "var layerThree = FindOCG('SupportLayer');\n" +
                "if (layerThree) layerThree.state = false;\n" +
                "var iTimer = app.setInterval('ToggleOCGs();', 1000);";

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
        canvas.addXObjectAt(pageCopy, 0, 0); // Correct method to add content
        canvas.endLayer();
    
        tempDoc.close();
        new File(tempPdf).delete(); // Clean up the temporary file
    }
}
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class OnPrint {
    public static final String DEST = "Out/Layer_Visibility_On_Print_realistic.pdf"; // Output path
    public static final String HTML1 = "1.html"; // Adjust paths as needed for HTML of layer 1
    public static final String HTML2 = "3.html"; // Adjust paths as needed for HTML of layer 1

    public static void main(String[] args) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(DEST));
        pdfDoc.addNewPage();

        PdfLayer layer1 = createLayer("Layer 1: HTML1 (Screen)", true, pdfDoc);
        PdfLayer layer2 = createLayer("Layer 2: HTML2 (Print)", false, pdfDoc);
        layer2.setOnPanel(false);
        layer2.setPrint("Print", true);
        layer1.setPrint("Never", false);

        drawHtmlOnLayer(pdfDoc, HTML1, layer1);
        drawHtmlOnLayer(pdfDoc, HTML2, layer2);

        // Java script
        String js = "var ocgs = this.getOCGs(); if(ocgs.length > 1) { ocgs[0].state = false; ocgs[1].state = true; } else { app.alert('OCG layers not found!'); }";
        PdfAction willPrintAction = PdfAction.createJavaScript(js);
        pdfDoc.getCatalog().setAdditionalAction(PdfName.WP, willPrintAction);

        pdfDoc.close();
    }

    private static PdfLayer createLayer(String name, boolean initialState, PdfDocument pdfDoc) {
        PdfLayer layer = new PdfLayer(name, pdfDoc);
        layer.setOn(initialState);
        return layer;
    }

    private static void drawHtmlOnLayer(PdfDocument pdfDoc, String htmlFile, PdfLayer layer) throws IOException {
        String tempPdf = "temp.pdf";
        HtmlConverter.convertToPdf(new FileInputStream(htmlFile), new FileOutputStream(tempPdf));
        
        File tempPdfFile = new File(tempPdf);
        PdfDocument tempDoc = new PdfDocument(new PdfReader(tempPdf));
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage().newContentStreamBefore(), pdfDoc.getLastPage().getResources(), pdfDoc);
        canvas.beginLayer(layer);
        PdfFormXObject pageCopy = tempDoc.getFirstPage().copyAsFormXObject(pdfDoc);
        canvas.addXObjectAt(pageCopy, 0, 0);
        canvas.endLayer();
        
        tempDoc.close();
        tempPdfFile.delete(); // Clean up
    }
}

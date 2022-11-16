package utd.dallas.backend;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class Rename {

    public static void main(String[] args) throws IOException {
        File forms = new File("src/main/resources/utd/dallas/backend/Forms");
        File[] listOfForms = forms.listFiles();
        PDDocument pdfDocument = null;

        Scanner input = new Scanner(System.in);

        for (File file : listOfForms) {
            System.out.println(file.getName());
            pdfDocument = PDDocument.load(file);
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            Iterator<PDField> fieldTreeIterator = acroForm.getFieldIterator();

            while (fieldTreeIterator.hasNext()) {
                PDField f = fieldTreeIterator.next();
                COSDictionary obj = f.getCOSObject();
                if (f.getClass().equals(PDTextField.class)) {
                    System.out.println(f.getAlternateFieldName());
                    System.out.print("Enter new name: ");
                    String s = input.next();
                    if(!s.isEmpty())
                        f.setAlternateFieldName(input.next());
                }
            }
            PDAcroForm finalForm = new PDAcroForm(pdfDocument);
            pdfDocument.getDocumentCatalog().setAcroForm(acroForm);
            pdfDocument.save(file);

        }

    }
}

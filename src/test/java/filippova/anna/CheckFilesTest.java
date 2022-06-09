package filippova.anna;

import com.codeborne.pdftest.PDF;
import com.codeborne.pdftest.matchers.ContainsExactText;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

public class CheckFilesTest {

    ClassLoader cl = CheckFilesTest.class.getClassLoader();

    @Test
    void zipParsingTest() throws Exception {
        ZipFile zipFile = new ZipFile(new File("src/test/resources/zip/zipfile.zip"));
        ZipInputStream zipInputStream = new ZipInputStream(cl.getResourceAsStream("zip/zipfile.zip"));
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                if (entry.getName().equals("pdffile.pdf")) {
                    PDF pdf = new PDF(inputStream);
                    Assertions.assertThat(entry.getName()).isEqualTo("pdffile.pdf");
                    assertThat(pdf, new ContainsExactText("pdf"));
                }
                if (entry.getName().equals("xlsxfile.xlsx")) {
                    XLS xls = new XLS(inputStream);
                    Assertions.assertThat(entry.getName()).isEqualTo("xlsxfile.xlsx");
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("xlsx");
                }
                if (entry.getName().equals("csvfile.csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    List<String[]> content = reader.readAll();
                    org.assertj.core.api.Assertions.assertThat(content).contains(
                            new String[]{"check", "array"}
                    );
                }
            }
        }
    }
}

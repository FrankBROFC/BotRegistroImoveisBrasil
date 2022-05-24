package br.com.frank.botRIB.utils;

import br.com.frank.botRIB.model.Serventia;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ExcelUtils {
    public void criarArquivo(List<Serventia> serventias) {
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd-MM-yyyy").format(dataHoraAtual);

        File path = new File("C:\\Registro de Imóveis\\");

        if (!path.exists()) path.mkdirs();

        try(XSSFWorkbook planilha = new XSSFWorkbook()) {
            XSSFSheet abaMain = planilha.createSheet("Planilha 1");

            //Cabeçalho
            Row row = abaMain.createRow(0);
            row.createCell(0).setCellValue("ESTADO");
            row.createCell(1).setCellValue("CIDADE");
            row.createCell(2).setCellValue("SERVENTIA");
            row.createCell(3).setCellValue("CNS");
            row.createCell(4).setCellValue("RESPONSÁVEL");
            row.createCell(5).setCellValue("ENDEREÇO");
            row.createCell(6).setCellValue("CEP");
            row.createCell(7).setCellValue("TELEFONE");
            row.createCell(8).setCellValue("EMAIL");

            // Preencher as linhas com os dados
            int contador = 1;

            for (Serventia serventia:
                 serventias) {
                row = abaMain.createRow(contador);

                row.createCell(0).setCellValue(serventia.getEstado());
                row.createCell(1).setCellValue(serventia.getCidade());
                row.createCell(2).setCellValue(serventia.getServentia());
                row.createCell(3).setCellValue(serventia.getCns());
                row.createCell(4).setCellValue(serventia.getResponsavel());
                row.createCell(5).setCellValue(serventia.getEndereco());
                row.createCell(6).setCellValue(serventia.getCep());
                row.createCell(7).setCellValue(serventia.getTelefone());
                row.createCell(8).setCellValue(serventia.getEmail());

                contador++;
            }

            try (OutputStream outputStream = new FileOutputStream(path + "\\Planilha Serventia " + data + ".xlsx")) {
                planilha.write(outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

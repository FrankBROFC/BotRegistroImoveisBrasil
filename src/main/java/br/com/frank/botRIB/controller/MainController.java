package br.com.frank.botRIB.controller;

import br.com.frank.botRIB.model.Serventia;
import br.com.frank.botRIB.utils.ExcelUtils;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {
    private final ExcelUtils excelUtils;

    @Autowired
    public MainController(ExcelUtils excelUtils) {
        this.excelUtils = excelUtils;
    }

    @PostConstruct
    public void run() {
        List<Serventia> serventias = new ArrayList<>();

        excelUtils.criarArquivo(serventias);

        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        FirefoxDriver driver = null;

        try {
            driver = new FirefoxDriver();

            driver.get("https://www.registrodeimoveis.org.br/atendimento-remoto");

            Select selectEstados = new Select(driver.findElement(By.id("estado")));

            for (int i = 1; i < selectEstados.getOptions().size(); i++) {
                selectEstados.selectByIndex(i);

                Select selectCidades = new Select(driver.findElement(By.id("cidade")));

                for (int j = 1; j < selectCidades.getOptions().size(); j++) {
                    MultipartBody response = Unirest.post("https://www.registrodeimoveis.org.br/includes/agendamento/consultas-ajax.php")
                            .field("acao", "serventia")
                            .field("uf", selectEstados.getOptions().get(i).getAttribute("value"))
                            .field("cidade", selectCidades.getOptions().get(j).getAttribute("value"))
                            .field("especialidade", "especialidade_ri");

                    JSONArray body = response.asJson().getBody().getArray();

                    if (body.getJSONObject(0).length() > 2) {
                        for (int k = 0; k < body.length(); k++) {
                            try {
                                response = Unirest.post("https://www.registrodeimoveis.org.br/includes/agendamento/consultas-ajax.php")
                                        .field("acao", "dados_serventia")
                                        .field("id", body.getJSONObject(k).getString("value"));

                                JSONObject object = response.asJson().getBody().getObject();
                                Serventia serventia = new Serventia();

                                serventia.setEstado(selectEstados.getOptions().get(i).getText());
                                serventia.setCidade(selectCidades.getOptions().get(j).getText());
                                serventia.setCns(object.getString("codigo_cns"));
                                serventia.setResponsavel(object.getString("oficial_nome"));
                                serventia.setCep(object.getString("endereco_cep"));
                                serventia.setTelefone("(" + object.getString("telefone_1_ddd") + ") " + object.get("telefone_1_numero"));
                                serventia.setServentia(object.getString("nome"));
                                serventia.setEmail(object.getString("publico_email"));
                                serventia.setEndereco(object.getString("endereco_tipo_longradouro") + " " + object.getString("endereco_logradouro") + ", " + object.getString("endereco_numero") + " - " + object.getString("endereco_bairro"));

                                serventias.add(serventia);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }

            excelUtils.criarArquivo(serventias);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(driver)) driver.quit();
        }
    }
}

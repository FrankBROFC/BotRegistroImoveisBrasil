# Bot Registro de Imoveis do Brasil
Criação de um bot para capturar dados da plataforma Registro de Imóveis do Brasil

Esse bot acessa a url: https://www.registrodeimoveis.org.br/atendimento-remoto, através do Selenium capturando todos os estados e municipios, 
logo após utilizando o Unirest é feito requisições para capturar os dados que vem em JSON e finaliza com a criação de uma planilha utilando o Apache POI.

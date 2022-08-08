<h1 align="center"> ERENO-UI </h1>
<h5 align="left"> Ferramenta com Interface Gráfica para gerar um dataset com mensagens dos protocolos GOOSE e SV para subestações elétricas.
A ferramenta possui diversas interfaces, ou módulos: </h5>

- [x] Módulo de Configuração de Parâmetros;
- [x] Módulo de Definição de Modelos de Ataques;
- [x] Módulo de Definição de Corrente e Tensão;
- [x] Módulo de Download do Dataset Final.


### Índice

* [Ambiente de Teste](#ambiente-de-teste)
* [Preparando o Ambiente](#preparando-o-ambiente)
* [Parâmetros Disponíveis](#parametros-disponiveis)
* [Exemplo de Uso](#exemplo-de-uso)

<div id="ambiente-de-teste"/>


### 🖱️ Ambiente de Teste - Execução Local

Configurações do Computador
```
Sistema Operacional: Windows 10
Processador: Intel(R) Core(TM) i5-10300H CPU @ 2.50GHz 2.50 GHz
Memória RAM: 16 GB
Sistema Operacional de 64 bits
```

Ambiente de desenvolvimento
```
IntelliJ IDEA 2022.2 (Ultimate Edition)
Runtime version: 17.0.3+7-b469.32 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
```

Ferramentas
```
Java SE JDK 11.0.16 <a href="https://www.oracle.com/br/java/technologies/javase/jdk11-archive-downloads.html"> download </a>
Apache Tomcat 9.0.65 (<a href="https://tomcat.apache.org/download-90.cgi"> download </a>)
```


<div id="preparando-o-ambiente"/>

### ⚙️Preparando o ambiente
Instalação do Git
```
sudo apt-get install git -y
```
Clone o Repositório
```
git clone https://github.com/sequincozes/ereno.git
```
Entre na pasta principal do projeto clonado e dê permissões para os arquivos.
```
cd ereno
```
Instale os software necessários utilizando os comandos:
```
- sudo snap install wamp
```

<div id="parametros-disponiveis"/>

### 📌 Parâmetros disponíveis:

```
max_time;
min_time;
etc..
```

<div id="exemplo-de-uso"/>

### 👨‍💻 Exemplo de uso
Entre no diretório principal:
```
cd ereno
run ereno
```

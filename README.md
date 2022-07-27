<h1 align="center"> ERENO </h1>
<h5 align="left"> Ferramenta com Interface Gráfica para gerar um dataset com mensagens dos protocolos GOOSE e SV para subestações elétricas.
A ferramenta possui diversas interfaces, ou módulos: </h5>

- [x] Módulo de Configuração de Parâmetros;
- [x] Módulo de Definição de Modelos de Ataques;
- [x] Módulo de Definição de Corrente e Tensão;
- [x] Módulo de Download do Dataset Final.


### Índice

* [Ambiente de Teste](#ambiente-de-teste)
* [Preparando o Ambiente (Linux)](#preparando-o-ambiente)
* [Parâmetros Disponíveis](#parametros-disponiveis)
* [Exemplo de Uso](#exemplo-de-uso)

<div id="ambiente-de-teste"/>


### 🖱️ Ambiente de Teste 

Foram previstas duas formas para testar a ferramenta proposta: 
> 1º Forma (e mais simples): Consiste no acesso ao website <a href="ereno.com">. Nessa forma, não é necessário executar o back-end do ERENO, pois estamos rodando o mesmo em um servidor na Internet. 
> 2º Forma: A segunda forma consiste na execução local. Portanto, é necessário executar o back-end do ERENO (arquivo .jar), bem como acessar a interface gráfica via localhost.

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

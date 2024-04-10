<%@ page import="java.io.PrintWriter" %>
<%@ page import="br.ufu.facom.ereno.api.GooseFlow" %>
<%@ page import="br.ufu.facom.ereno.utils.GSVDatasetWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <title>Configurações - ERENO</title>
    <meta content="" name="description">
    <meta content="" name="keywords">

    <!-- Favicons -->
    <link href="assets/img/favicon.png" rel="icon">
    <link href="assets/img/apple-touch-icon.png" rel="apple-touch-icon">

    <!-- Google Fonts -->
    <link href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Nunito:300,300i,400,400i,600,600i,700,700i|Poppins:300,300i,400,400i,500,500i,600,600i,700,700i"
          rel="stylesheet">

    <!-- Vendor CSS Files -->
    <link href="assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
    <link href="assets/vendor/boxicons/css/boxicons.min.css" rel="stylesheet">
    <link href="assets/vendor/quill/quill.snow.css" rel="stylesheet">
    <link href="assets/vendor/quill/quill.bubble.css" rel="stylesheet">
    <link href="assets/vendor/remixicon/remixicon.css" rel="stylesheet">
    <link href="assets/vendor/simple-datatables/style.css" rel="stylesheet">

    <!-- Template Main CSS File -->
    <link href="assets/css/style.css" rel="stylesheet">

</head>

<body>

<!-- ======= Header ======= -->
<header id="header" class="header fixed-top d-flex align-items-center">

    <div class="d-flex align-items-center justify-content-between">
        <a href="index.jsp" class="logo d-flex align-items-center">
            <img src="assets/img/logo-ereno.png" alt="">
            <span class="d-none d-lg-block">ERENO-UI</span>
        </a>
        <i class="bi bi-list toggle-sidebar-btn"></i>
    </div><!-- End Logo -->

    <div class="search-bar">
        <form class="search-form d-flex align-items-center" method="POST" action="#">
            <input type="text" name="query" placeholder="Pesquisar configurações ou datasets"
                   title="Enter search keyword">
            <button type="submit" title="Pesquisar"><i class="bi bi-search"></i></button>
        </form>
    </div><!-- End Search Bar -->

    <nav class="header-nav ms-auto">
        <ul class="d-flex align-items-center">
            <li class="nav-item dropdown pe-3">
                <a class="nav-link nav-profile d-flex align-items-center pe-0" href="en/goose-message.jsp">
                    <img src="assets/img/en-pt.png" alt="Language" class="rounded-circle">
                    <div style="display: none;"><%=GSVDatasetWriter.english = false%>
                    </div>
                </a>
            </li><!-- End Profile Nav -->
        </ul>
    </nav><!-- End Icons Navigation -->
</header><!-- End Header -->

<!-- ======= Sidebar ======= -->
<aside id="sidebar" class="sidebar">

    <ul class="sidebar-nav" id="sidebar-nav">

        <li class="nav-item">
            <a class="nav-link " data-bs-target="#forms-nav" data-bs-toggle="collapse" href="#">
                <i class="bi bi-journal-text"></i><span>Configurações</span><i class="bi bi-chevron-down ms-auto"></i>
            </a>
            <ul id="forms-nav" class="nav-content expand " data-bs-parent="#sidebar-nav">
                <li>
                    <a href="ied-configs.jsp">
                        <i class="bi bi-circle"></i><span>Configurações do Ambiente</span>
                    </a>
                    <a href="goose-message.jsp">
                        <i class="bi bi-circle"></i><span style="color: #06c !important">Fluxos de Mensagens</span>
                    </a>
                    <a href="upload-samples.jsp">
                        <i class="bi bi-circle"></i><span>Upload de Leituras</span>
                    </a>
                    <a href="attack-definitions.jsp">
                        <i class="bi bi-circle"></i><span>Ataques</span>
                    </a>
                </li>
            </ul>
        </li><!-- End Forms Nav -->

        <li class="nav-item">
            <a class="nav-link collapsed" href="download-datasets.jsp">
                <i class="bi bi-download"></i>
                <span>Datasets</span>
            </a>
        </li><!-- End Dashboard Nav -->
    </ul>
</aside><!-- End Sidebar-->

<main id="main" class="main">

    <div class="pagetitle">
        <h1>Configurações</h1>
        <nav>
            <ol class="breadcrumb">
                <li class="breadcrumb-item">Configurações do IED</li>
                <li class="breadcrumb-item" active><a style="color: #06c"> Fluxo de Mensagens </a></li>
                <li class="breadcrumb-item"> Upload de Leituras</li>
                <li class="breadcrumb-item"> Ataques</li>
                <li class="breadcrumb-item"> Download do Dataset</li>
            </ol>
        </nav>
    </div><!-- End Page Title -->

    <section class="section">
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Configurações do IED</h5>

                        <!-- General Form Elements -->
                        <form action="goose-flow" method="post" accept-charset="utf-8">
                            <%
                                // Loading saved values to update form
                                GooseFlow.ECF.loadConfigs(application);
                            %>
                            <!-- Floating Labels Form -->
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="goID" name="goID"
                                               placeholder="IntLockA" value="<%=GooseFlow.ECF.goID%>">
                                        <label for="goID">Identificação do Fluxo GOOSE</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="numberOfMessages"
                                               name="numberOfMessages"
                                               placeholder="Número de Mensagens Periódicas"
                                               value="<%=GooseFlow.ECF.numberOfMessages%>">
                                        <label for="numberOfMessages">Número de Mensagens Periódicas</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="ethSrc" name="ethSrc"
                                               placeholder="01:0c:cd:01:2f:78" value="<%=GooseFlow.ECF.ethSrc%>">
                                        <label for="ethSrc">Endereço MAC de Origem</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="ethDst" name="ethDst"
                                               placeholder="01:0c:cd:01:2f:77" value="<%=GooseFlow.ECF.ethDst%>">
                                        <label for="ethDst">Endereço MAC de Destino</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input class="form-control" id="ethType" name="ethType" placeholder="0x88B8"
                                               type="text" value="<%=GooseFlow.ECF.ethType%>">
                                        <label for="ethDst">EtherType</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="gooseAppid" name="gooseAppid"
                                               placeholder="0x00003001" value="<%=GooseFlow.ECF.gooseAppid%>">
                                        <label for="gooseAppid">Identificação da Aplicação GOOSE (AppID)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="TPID" name="TPID"
                                               placeholder="0x00003001" value="<%=GooseFlow.ECF.TPID%>">
                                        <label for="TPID">Tag de Identificação de Aplicação (TPID)</label>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="col-sm-10">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="ndsCom"
                                                   name="ndsCom" <%=isChecked(GooseFlow.ECF.ndsCom)%>>
                                            <label class="form-check-label" for="ndsCom">Precisa de
                                                comissionamento?</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="col-sm-10">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="test"
                                                   name="test" <%=isChecked(GooseFlow.ECF.test)%>>
                                            <label class="form-check-label" for="test">É um fluxo de teste?</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="col-sm-10">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="cbstatus"
                                                   name="cbstatus" <%=isChecked(GooseFlow.ECF.cbstatus)%>>
                                            <label class="form-check-label" for="cbstatus">O disjuntor está aberto
                                                (cBStatus)?</label>
                                        </div>
                                    </div>
                                </div>

                                <div>
                                    <button type="reset" class="btn btn-secondary"><a style="color:white ;"
                                                                                      href="ied-configs.jsp">Voltar</a>
                                    </button>
                                    <button type="submit" class="btn btn-primary">Próximo</button>
                                </div>
                            </div>

                        </form>
                    </div>

                </div><!-- End General Form Elements -->
            </div>
        </div>
    </section>

</main><!-- End #main -->

<!-- ======= Footer ======= -->
<footer id="footer" class="footer">
    <div class="copyright">
        Copyright &copy; 2022 <a href="https://github.com/sequincozes/ereno">ERENO</a>
        <br>
        Todos os direitos reservados.
    </div>
</footer><!-- End Footer -->

<a href="#" class="back-to-top d-flex align-items-center justify-content-center"><i
        class="bi bi-arrow-up-short"></i></a>

<!-- Vendor JS Files -->
<script src="assets/vendor/apexcharts/apexcharts.min.js"></script>
<script src="assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="assets/vendor/chart.js/chart.min.js"></script>
<script src="assets/vendor/echarts/echarts.min.js"></script>
<script src="assets/vendor/quill/quill.min.js"></script>
<script src="assets/vendor/simple-datatables/simple-datatables.js"></script>
<script src="assets/vendor/tinymce/tinymce.min.js"></script>
<script src="assets/vendor/php-email-form/validate.js"></script>

<!-- Template Main JS File -->
<script src="assets/js/main.js"></script>

</body>

</html>
<%!
    private String isChecked(boolean isChecked) {
        if (isChecked) {
            return "checked";
        } else {
            return "";
        }
    }
%>
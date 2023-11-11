<%@ page import="br.ufu.facom.ereno.utils.DatasetWritter" %>
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
                <a class="nav-link nav-profile d-flex align-items-center pe-0" href="en/upload-samples.jsp">
                    <img src="assets/img/en-pt.png" alt="Language" class="rounded-circle">
                    <div style="display: none;"><%=DatasetWritter.english = false%>
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
                        <i class="bi bi-circle"></i><span>Fluxos de Mensagens</span>
                    </a>
                    <a href="upload-samples.jsp">
                        <i class="bi bi-circle"></i><span style="color: #06c !important">Upload de Leituras</span>
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
                <li class="breadcrumb-item"> Fluxo de Mensagens</li>
                <li class="breadcrumb-item" active><a style="color: #06c"> Upload de Leituras </a></li>
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
                        <h5 class="card-title">Upload de Amostragens de Corrente e Tensão</h5>
                        <!-- General Form Elements -->
                        <form method="post" action="sv-samples" enctype="multipart/form-data">
                            <!-- Floating Labels Form -->
                            <div class="row g-3">
                                <div class="col-md-12">
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="sv"
                                                   onclick="generateSv();">
                                            <label class="form-check-label" for="sv">Gerar mensagens SV</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12" id="div-checkbox-upload" style="display: none">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="custom" name="custom"
                                                   onclick="uploadSv();">
                                            <label class="form-check-label" for="custom">Fazer upload de arquivo
                                                personalizado (do contrário, usa-se o padrão)</label>
                                        </div>
                                        <br>
                                    </div>

                                    <div class="col-md-12" id="div-upload" style="display: none">
                                        <div class="form-floating mb-3" style="line-height: 0.5">
                                            <input type="file" class="form-control" id="samples"
                                                   name="multiPartServlet"
                                                   style="min-height: 40px">
                                            <label for="samples">Amostras de Corrente e Tensão</label>
                                        </div>
                                    </div>

                                    <div>
                                        <br>
                                        <button type="reset" class="btn btn-secondary"><a style="color:white ;"
                                                                                          href="goose-message.jsp">Voltar</a>
                                        </button>
                                        <input type="submit" value="Próximo" class="btn btn-primary">

                                    </div>
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
        Copyright &copy; 2022 <a href="https://www.facom.ufu.br/~sequincozes/">ERENO</a>
        <br>
        Todos os direitos reservados.
    </div>
</footer><!-- End Footer -->

<a href="#" class="back-to-top d-flex align-items-center justify-content-center"><i
        class="bi bi-arrow-up-short"></i></a>

<script>
    var showCheckboxUpload = false;

    function generateSv() {
        if (showCheckboxUpload) {
            document.getElementById("div-checkbox-upload").style.display = "none";
            showCheckboxUpload = false;
        } else {
            document.getElementById("div-checkbox-upload").style.display = "block";
            showCheckboxUpload = true;
        }
    }

    var showUpload = false;

    function uploadSv() {
        if (showUpload) {
            document.getElementById("div-upload").style.display = "none";
            showUpload = false;
        } else {
            document.getElementById("div-upload").style.display = "block";
            showUpload = true;
        }
    }
</script>

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
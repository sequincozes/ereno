<%@ page import="br.ufu.facom.ereno.api.Attacks" %>
<%@ page import="br.ufu.facom.ereno.SingleSource" %>
<%@ page import="br.ufu.facom.ereno.dataExtractors.GSVDatasetWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <title>Settings - ERENO</title>
    <meta content="" name="description">
    <meta content="" name="keywords">

    <!-- Favicons -->
    <link href="../assets/img/favicon.png" rel="icon">
    <link href="../assets/img/apple-touch-icon.png" rel="apple-touch-icon">

    <!-- Google Fonts -->
    <link href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,300i,400,400i,600,600i,700,700i|Nunito:300,300i,400,400i,600,600i,700,700i|Poppins:300,300i,400,400i,500,500i,600,600i,700,700i"
          rel="stylesheet">

    <!-- Vendor CSS Files -->
    <link href="../assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="../assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet">
    <link href="../assets/vendor/boxicons/css/boxicons.min.css" rel="stylesheet">
    <link href="../assets/vendor/quill/quill.snow.css" rel="stylesheet">
    <link href="../assets/vendor/quill/quill.bubble.css" rel="stylesheet">
    <link href="../assets/vendor/remixicon/remixicon.css" rel="stylesheet">
    <link href="../assets/vendor/simple-datatables/style.css" rel="stylesheet">

    <!-- Template Main CSS File -->
    <link href="../assets/css/style.css" rel="stylesheet">

</head>

<body>

<!-- ======= Header ======= -->
<header id="header" class="header fixed-top d-flex align-items-center">

    <div class="d-flex align-items-center justify-content-between">
        <a href="index.jsp" class="logo d-flex align-items-center">
            <img src="../assets/img/logo-ereno.png" alt="">
            <span class="d-none d-lg-block">ERENO-UI</span>
        </a>
        <i class="bi bi-list toggle-sidebar-btn"></i>
    </div><!-- End Logo -->

<%--    <div class="search-bar">--%>
<%--        <form class="search-form d-flex align-items-center" method="POST" action="#">--%>
<%--            <input type="text" name="query" placeholder="Search datasets and configs"--%>
<%--                   title="Enter search keyword">--%>
<%--            <button type="submit" title="Pesquisar"><i class="bi bi-search"></i></button>--%>
<%--        </form>--%>
<%--    </div><!-- End Search Bar -->--%>

    <nav class="header-nav ms-auto">
        <ul class="d-flex align-items-center">
            <li class="nav-item dropdown pe-3">
                <a class="nav-link nav-profile d-flex align-items-center pe-0" href="../attack-definitions.jsp">
                    <img src="../assets/img/en-pt.png" alt="Language" class="rounded-circle">
                    <div style="display: none;"><%=GSVDatasetWriter.english = true%>
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
                <i class="bi bi-journal-text"></i><span>Settings</span><i class="bi bi-chevron-down ms-auto"></i>
            </a>
            <ul id="forms-nav" class="nav-content expand " data-bs-parent="#sidebar-nav">
                <li>
                    <a href="ied-configs.jsp">
                        <i class="bi bi-circle"></i><span>IED Settings</span>
                    </a>
                    <a href="goose-message.jsp">
                        <i class="bi bi-circle"></i><span>GOOSE Flow</span>
                    </a>
                    <a href="upload-samples.jsp">
                        <i class="bi bi-circle"></i><span>SV Data Upload</span>
                    </a>
                    <a href="attack-definitions.jsp">
                        <i class="bi bi-circle"></i><span style="color: #06c !important">Attacks</span>
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
        <h1>Settings</h1>
        <nav>
            <ol class="breadcrumb">
                <li class="breadcrumb-item">IED Settings</li>
                <li class="breadcrumb-item"> GOOSE Flow</li>
                <li class="breadcrumb-item"> SV Data Upload</li>
                <li class="breadcrumb-item" active><a style="color: #06c"> Attacks </a></li>
                <li class="breadcrumb-item"> Dataset Download</li>
            </ol>
        </nav>
    </div><!-- End Page Title -->

    <section class="section">
        <div class="row">
            <div class="col-lg-8">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">Select attacks to compose the dataset:</h5>

                        <!-- General Form Elements -->
                        <form action="../attacks" method="post" accept-charset="utf-8">
                            <%
                                // Loading saved values to update form
                                try {
                                    Attacks.ECF.loadConfigs(application);
                                } catch (Exception exep) {
                                    exep.printStackTrace();
                                    response.getWriter().write(exep.getLocalizedMessage());
                                }
                            %>

                            <!-- Floating Labels Form -->
                            <div class="row g-3">
                                <div class="col-md-12">
                                    <div class="col-md-12">
                                        <div class="form-floating mb-3">
                                            <input type="text" class="form-control" id="datasetName" name="datasetName" required
                                                   placeholder="IED de Proteção" value="<%=Attacks.ECF.datasetName%>">
                                            <label for="datasetName">Dataset Name</label>
                                        </div>
                                    </div>

                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="legitimate" required
                                                   name="legitimate" <%=isChecked(Attacks.ECF.legitimate)%>>
                                            <label class="form-check-label" for="legitimate">Include Legitimate Messages</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="random-replay"
                                                   name="random-replay" <%=isChecked(Attacks.ECF.randomReplay)%>
                                            <label class="form-check-label" for="random-replay"> Random Replay Attacks</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="inverse-replay"
                                                   name="inverse-replay" <%=isChecked(Attacks.ECF.inverseReplay)%>
                                            <label class="form-check-label" for="inverse-replay"> Inverse Replay Attacks</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="masquerade-outage"
                                                   name="masquerade-outage" <%=isChecked(Attacks.ECF.masqueradeOutage)%>
                                            <label class="form-check-label" for="masquerade-outage"> Masquerade Attacks (Outage)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="masquerade-damage"
                                                   name="masquerade-damage" <%=isChecked(Attacks.ECF.masqueradeDamage)%>
                                            <label class="form-check-label" for="masquerade-damage"> Masquerade Attacks (Equipment Damage)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="random-injection"
                                                   name="random-injection" <%=isChecked(Attacks.ECF.randomInjection)%>
                                            <label class="form-check-label" for="random-injection"> Random Injection Attacks</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="high-st-num"
                                                   name="high-st-num" <%=isChecked(Attacks.ECF.highStNum)%>>
                                            <label class="form-check-label" for="high-st-num"> DoS Attacks
                                                (High-StNum)</label>
                                        </div>
                                    </div>
                                    <div class="col-sm-12">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" id="flooding"
                                                   name="flooding" <%=isChecked(Attacks.ECF.flooding)%>>
                                            <label class="form-check-label" for="flooding"> DoS Attacks
                                                (Flooding)</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <br>
                                <button type="reset" class="btn btn-secondary"><a style="color:white ;"
                                                                                  href="upload-samples.jsp">Back</a>
                                </button>
                                <button type="submit" class="btn btn-primary">Finish</button>
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
        All rights reserved.
    </div>
</footer><!-- End Footer -->

<a href="#" class="back-to-top d-flex align-items-center justify-content-center"><i
        class="bi bi-arrow-up-short"></i></a>

<!-- Vendor JS Files -->
<script src="../assets/vendor/apexcharts/apexcharts.min.js"></script>
<script src="../assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="../assets/vendor/chart.js/chart.min.js"></script>
<script src="../assets/vendor/echarts/echarts.min.js"></script>
<script src="../assets/vendor/quill/quill.min.js"></script>
<script src="../assets/vendor/simple-datatables/simple-datatables.js"></script>
<script src="../assets/vendor/tinymce/tinymce.min.js"></script>
<script src="../assets/vendor/php-email-form/validate.js"></script>

<!-- Template Main JS File -->
<script src="../assets/js/main.js"></script>

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
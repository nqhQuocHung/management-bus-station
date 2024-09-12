<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Companies</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #469A9E;
        }
        .add-company-btn {
            display: inline-block;
            margin-bottom: 10px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            position: fixed;
            top: 60px;
            right: 18px;
        }
        .even {
            background-color: #f2f2f2;
        }
        .odd {
            background-color: #ffffff;
        }
    </style>
</head>
<body>
<h1 align="center">Transportation Company</h1>
<a href="<c:url value="/admin/company" />" class="add-company-btn"> +Add new</a>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Phone</th>
        <th>Email</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody id="companyTable">
    <!-- Content will be loaded via AJAX -->
    </tbody>
</table>
<script>
    $(document).ready(function() {
        loadCompanies();

        function loadCompanies() {
            axios.get('http://localhost:8080/busstation/api/v1/transportation_company/list')
                .then(function(response) {
                    console.log("Data received:", response.data);
                    var data = response.data.results;
                    var tableContent = '';
                    data.forEach(function(company, index) {
                        var rowClass = index % 2 === 0 ? 'even' : 'odd';
                        tableContent += '<tr class="' + rowClass + '">';
                        tableContent += '<td>' + company.id + '</td>';
                        tableContent += '<td>' + company.name + '</td>';
                        tableContent += '<td>' + company.phone + '</td>';
                        tableContent += '<td>' + company.email + '</td>';
                        tableContent += '<td>' +
                            '<a href="http://localhost:8080/busstation/admin/company_detail?id=' + company.id + '">Edit</a> ' +
                            '<button class="delete-btn" data-id="' + company.id + '">Delete</button>' +
                            '</td>';
                        tableContent += '</tr>';
                    });
                    $('#companyTable').html(tableContent);

                    $('.delete-btn').on('click', function() {
                        var companyId = $(this).data('id');
                        if (confirm('Are you sure you want to delete this company?')) {
                            deleteCompany(companyId);
                        }
                    });
                })
                .catch(function(error) {
                    console.error("Failed to load data:", error);
                });
        }

        function deleteCompany(companyId) {
            axios.delete('http://localhost:8080/busstation/api/v1/transportation_company/' + companyId)
                .then(function() {
                    loadCompanies();
                })
                .catch(function(error) {
                    console.error("Failed to delete company:", error);
                });
        }
    });
</script>
</body>
</html>

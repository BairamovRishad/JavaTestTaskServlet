function Employees() {
    var self = this;
    var lastSearchTerm = "*";

    this.init = function() {
        $(document).on('submit', '#saveEmployeeForm', function() {
            var $form = $(this);

            var employee = $form.serializeObject();
            var type = employee.id == "0" ? "PUT" : "POST";

            $.ajax({
                type: type,
                url: $form.attr("action"),
                contentType: 'application/json',
                data: JSON.stringify(employee),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
                },
                success: function() {
                    common.modal.close();
                    updateEmployeesTable();
                },
                error: function(xhr) {
                    common.modal.show(xhr.responseText);
                }
            });

            return false;
        });

        $('.search-btn').click(function() {
            var term = $('input[type="search"]').val();
            search(term);
            return false;
        });

        $('.reset-btn').click(function() {
            search();
            $('input[type="search"]').val("");
            return false;
        });

        $('input[type="search"]').keypress(function(event) {
            if (event.which == 13) {
                event.preventDefault();
                var term = $(this).val();
                search(term);
            }
        });
    };

    function search(term) {
        // if term is empty then request all records
        term = term || "*";
        lastSearchTerm = term;
        var url = $('input[type="search"]').data('url');

        $.get(url, {term: term})
            .done(function(employeesTable) {
                $('.employees-table').html(employeesTable);
                $('.noresults').hide();
                $('.employees-table').show();
            })
            .fail(function() {
                $('.noresults').show();
                $('.employees-table').hide();
            }
        );
    }

    function updateEmployeesTable() {
        search(lastSearchTerm);
    }
}

var employees = null;
$(document).ready(function() {
    employees = new Employees();
    employees.init();
});

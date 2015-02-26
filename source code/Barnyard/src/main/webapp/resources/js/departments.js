function Departments() {
    var self = this;

    this.init = function() {
        $(document).on('submit', '#saveDepartmentForm', function() {
            var $form = $(this);

            var department = $form.serializeObject();
            var type = department.id == "0" ? "PUT" : "POST";

            $.ajax({
                type: type,
                url: $form.attr("action"),
                contentType: 'application/json',
                data: JSON.stringify(department),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
                },
                success: function() {
                    common.modal.close();
                    var sourceUrl = $(this).data('source');
                    updateDepartmentsTable(sourceUrl);
                },
                error: function(xhr) {
                    common.modal.show(xhr.responseText);
                }
            });

            return false;
        });
    };

    function updateDepartmentsTable(url) {
        $.get(url, function(departmentTable) {
            $('.departments-table').html(departmentTable);
        });
    }
}

var departments = null;
$(document).ready(function() {
    departments = new Departments();
    departments.init();
});
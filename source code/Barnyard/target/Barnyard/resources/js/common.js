function Common() {
    var self = this;
    this.modal = new Modal();
    this.csrfToken = $("meta[name='_csrf']").attr("content");
    this.csrfHeader =  $("meta[name='_csrf_header']").attr("content");

    this.init = function() {
        $(document).on('click', '.details-btn, .edit-btn, .create-btn', function() {
            $.ajax({
                type: "GET",
                url: $(this).attr('href'),
                contentType: 'application/json',
                success: function(response) {
                    self.modal.show(response);
                },
                error: function(xhr) {
                    common.modal.show(xhr.responseText);
                }
            });

            return false;
        });
    };

    this.showError = function(xhr) {
        var responseTitle = $(xhr.responseText).filter('title').get(0);
        alert($(responseTitle).text());
    };
}

var common = null;
$(document).ready(function() {
    common = new Common();
    common.init();
});

function Modal() {
    this.show = function(data) {
        $(".modal-backdrop").remove();
        var popupWrapper = $("#PopupWrapper");
        popupWrapper.empty();
        popupWrapper.html(data);
        // var popup = $(".modal", popupWrapper);
        $(".modal", popupWrapper).modal();
    };

    this.close = function() {
        setTimeout(function() {
            $('.modal').modal('hide');
        }, 170);
    };
}

(function($) {
    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };
})(jQuery);
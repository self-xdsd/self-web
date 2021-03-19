/**
 * Confirm dialog.
 */
var confirmDialog = (function ($) {

    /**
     * Get the dialog element. If dialog is not attached to "body",
     * a new element will be created.
     * @returns Dialog element.
     */
    function getDialogEl() {
        var dialog = $('#confirm-dialog');
        if (!dialog.length) {
            dialog = $('<div id="confirm-dialog" class="modal fade">' +
                '<div class="modal-dialog">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<h3 id="dialog-title" style="width:100%"></h3>' +
                '<a class="close" data-dismiss="modal" >&times;</a>' +
                '</div>' +
                '<div class="modal-body">' +
                '<p id="dialog-body"></p>' +
                '</div>' +
                '<div class="modal-footer">' +
                '<a href="#!" class="btn" data-dismiss="modal">Cancel</a>' +
                '<a href="#!" id="dialog-confirm-btn" class="btn btn-primary">OK</a>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>');
            $("body").append(dialog);
        }
        return dialog;
    }

    return {
        /**
         * Creates a confirm dialog.
         * @param {String} body  Required dialog body.
         * @param {String} title Optional title. Default is "Warning".
         * @param {String} answer Optional answer. Default is "OK";
         */
        create: function (body, title, answer) {
            return new Promise(function (resolve, reject) {
                var dialog = getDialogEl();
                var confirmBtn = dialog.find("#dialog-confirm-btn");
                dialog.find("#dialog-title").text(title || "Warning");
                dialog.find("#dialog-confirm-btn").text(answer || "OK");
                dialog.find("#dialog-body").text(body);
                confirmBtn.click(function(event) {
                    dialog.modal("hide");
                    resolve();
                });
                //remove all listeners after `hide` is completed
                dialog.on("hidden.bs.modal", function(){
                    confirmBtn.off("click")
                    $(this).off()
                })
                dialog.modal("show");
            });
        }
    };
})(jQuery);
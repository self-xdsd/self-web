/**
* The module of "getAndAddContracts".

* This pattern - self invoked function - ensure the function is called
* with the "jQuery" global and "contractsService" global module right after added the file to <script>.
* This is equivalent of manually:
* var module = function($, service){}
* module(jQuery,contractsService);
*
* Whe could have used "$" alias of jQuery global variable without injecting it,
* but this a good practice since "$" is not "reserved" to jQuery.
* Now "$" is just a function argument name and not the jQuery
* alias anymore. It could named "foo" fo ex, thus making a call like foo("#contracts") valid.
*/
var projectContractsCount = -1;
(function getAndAddContracts($, contractsService, usersService, confirmDialog){

    function getTasksOfContract(contract) {
        $("#tasksTable").dataTable().fnDestroy();
        $("#tasksTitle").html(
            " Tasks assigned to " + contract.id.contributorUsername
            + " (" + contract.id.role + ")"
        )
        $("#tasks").show();
        $("#tasksTable").DataTable({
            language: {
                loadingRecords: '<img src="/images/loading.svg" height="100">'
            },
            ajax: {
                url: "/api/projects/"
                    + contract.id.repoFullName
                    + "/contracts/" + contract.id.contributorUsername + "/tasks?role=" + contract.id.role,
                dataSrc: ""
            },
            columns: [
                {
                    data: "issueId",
                    render: function (data) {
                        var issueLink;
                        if (contract.id.provider == 'github') {
                            issueLink = 'https://github.com/'
                                + contract.id.repoFullName
                                + "/issues/"
                                + data;
                        } else if (contract.id.provider == 'gitlab') {
                            issueLink = 'https://gitlab.com/'
                                + contract.id.repoFullName
                                + "/issues/"
                                + data;
                        } else {
                            issueLink = '#';
                        }
                        return "<a href='" + issueLink + "' target='_blank'>#" + data + "</a>"
                    }
                },
                {
                    data: "assignmentDate",
                    render: (data) => data.split('T')[0]
                },
                {
                    data: "deadline",
                    render: (data) => data.split('T')[0]
                },
                {
                    data: "estimation",
                    render: (data) => data + " min."
                },
                {
                    data: "value",
                    render: (data) => formatEuro(data)
                }
            ]
        })
    }

    function getInvoicesOfContract(contract) {
        $("#invoicesTable").dataTable().fnDestroy();
        $("#invoicesTitle").html(
            " Invoices of " + contract.id.contributorUsername
            + " (" + contract.id.role + ")"
        )
        $("#invoices").show();
        $("#invoicesTable").DataTable({
            language: {
                loadingRecords: '<img src="/images/loading.svg" height="100">'
            },
            ajax: function(data, callback){
                $.ajax(
                    "/api/projects/"
                    + contract.id.repoFullName
                    + "/contracts/" + contract.id.contributorUsername
                    + "/invoices?role=" + contract.id.role,
                    {
                        type: "GET",
                        statusCode: {
                            200: function (invoices) {
                                callback({ data: invoices.map(invoiceAsTableRow(contract))});
                            }
                        }
                    }
                );
            },
            drawCallback: function(){
                $('[data-toggle="tooltip"]').tooltip();
                $("#invoicesTable .payInvoice").off();
                $("#invoicesTable .payInvoice").each(function () {
                    $(this).on(
                        "click",
                        function (event) {
                            event.preventDefault();
                            var message = "Are you sure you want to make this payment?"
                            if (activeWallet.type == 'FAKE') {
                                message += ' You are using a fake wallet, the payment will be fictive.'
                            }
                            confirmDialog
                                .create(message, "Warning", "Yes")
                                .then(() => {
                                    var table = $("#invoicesTable").DataTable();
                                    var row = table.row($(event.currentTarget).parents('tr'));
                                    var contract = {
                                        id: {
                                            contributorUsername: $(this).attr("data-contributor"),
                                            role: $(this).attr("data-role"),
                                            repoFullName:  $(this).attr("data-repo")
                                        }
                                    };
                                    var invoice = {
                                        id: row.data()[0]
                                    };
                                    payInvoice(invoice, contract, $(this));
                                });
                        }
                    );
                });
            }
        });
    }

    /**
     * Turn an Contract Invoice into a table row.
     * @param contract Contract.
     * @returns Function which will return and array of columns.
     */
    function invoiceAsTableRow(contract) {
        return function (invoice) {
            var status;
            var payIcon = "";
            var pdfHref = "/api/projects/"
                + contract.id.repoFullName
                + "/contracts/" + contract.id.contributorUsername + "/invoices/"
                + invoice.id
                + "/pdf?role=" + contract.id.role;
            var downloadLink = "<a href='" + pdfHref + "' target='_blank' title='Download Invoice' class='downloadInvoice'>"
                + "<i class='fa fa-file-pdf-o fa-lg'></i>"
                + "</a>  ";
            if (!invoice.isPaid) {
                var latestPayment = invoice.latestPayment;
                if(latestPayment === undefined) {
                    status = "Active";
                } else {
                    var timestamp = latestPayment.timestamp;
                    var failMessage;
                    if(timestamp.length > 0) {
                        timestamp = timestamp.split('T')[0];
                        failMessage = timestamp + ": " + latestPayment.failReason;
                    } else {
                        failMessage = latestPayment.failReason;
                    }
                    status = "Payment failed " + "<i class='fa fa-exclamation-triangle fa-lg' style='color:red;' aria-hidden='true' "
                        + "data-toggle='tooltip' data-placement='top' "
                        + "data-original-title=\"" + failMessage.replaceAll('"', '\'')+ "\">"
                        +"</i>";
                }
                var totalAmount = parseFloat(invoice.totalAmount.substring(0, invoice.totalAmount.length - 1)
                        .replace(",",".").trim())
                if (totalAmount >= 108.0) {
                    payIcon = "<a href='#' title='Pay Invoice' class='payInvoice' "
                        + "data-contributor='" + contract.id.contributorUsername + "' "
                        + "data-role='" + contract.id.role + "' data-repo='" + contract.id.repoFullName + "'>"
                        + "<i class='fa fa-credit-card fa-lg'></i>"
                        + "</a>";
                }
            } else {
                status = "Paid";
            }
            return [
                invoice.id,
                invoice.createdAt.split('T')[0],
                invoice.totalAmount,
                status,
                downloadLink + payIcon
            ];
        };
    }

    function handleError(error){
        if(error.validation){
            Object.entries(error.validation).forEach(function(fieldError) {
                $("." + fieldError[0] + "-error")
                    .text(fieldError[1])
                    .show();
            });
        }else{
            alert("Error: " + error);
        }
    }

    function disableForm(disabled){
         $('#username').prop("disabled", disabled);
         $('#hourlyRate').prop("disabled", disabled);
         $('#role').prop("disabled", disabled);
         $('#add').prop("disabled", disabled);
    }

    function clearFormErrors(){
         $(".username-error").hide();
         $(".hourlyRate-error").hide();
    }

    /**
    * Turn a Contract into a DataTable row.
    * @param {Object} contract Contract.
    * @return Row as Object.
    */
    function contractAsTableRowArray(contract) {
        return {
            contributorUsername: contract.id.contributorUsername,
            role: contract.id.role,
            hourlyRate: contract.hourlyRate,
            value: contract.value,
            options: { markedForRemoval: contract.markedForRemoval }
        };
    }

    $(document).ready(function () {

        var project = {
            owner: $("#owner").text(),
            name: $("#name").text(),
            provider: window.globalProvider.value,
        };

        window.globalProvider.onChange(function(value){
            project.provider = value;
            $('.provider').text(value.capitalize());
        });

        function loadContracts() {
            $("#contracts").dataTable().fnDestroy();
            $("#contracts").dataTable({
                language: {
                    loadingRecords: '<img src="/images/loading.svg" height="100">'
                },
                ajax: function (data, callback) {
                    contractsService
                        .getAll(project)
                        .then(function (contracts) {
                            //adding contracts to table
                            callback({ data: contracts.map(contractAsTableRowArray) });
                        })
                        .catch(handleError);
                },
                rowId: function (data) {
                    return data.contributorUsername + data.role;
                },
                columns: [
                    { data: "contributorUsername" },
                    { data: "role" },
                    { data: "hourlyRate" },
                    { data: "value" },
                    {
                        data: "options",
                        render: function (data, type) {
                            if (type === 'display') {
                                var removeRestoreIcon;
                                if (data.markedForRemoval == 'null') {
                                    removeRestoreIcon = "<a href='#' title='Mark Contract For Removal' class='removeContract'>"
                                        + "<i class='fa fa-trash fa-lg'></i>"
                                        + "</a>";
                                } else {
                                    var toolTipMessage = "This contract has been marked for removal on "
                                        + data.markedForRemoval.split('T')[0] + ". "
                                        + "No more tasks will be assigned to it and it will be removed after 30 days.";
                                    removeRestoreIcon = "<a href='#' title='Restore Contract' class='restoreContract'>"
                                        + "<i class='fa fa-recycle fa-lg'></i>"
                                        + "</a>  "
                                        + "<i class='fa fa-exclamation-circle fa-lg fakeWalletInfo' style='color:red;' aria-hidden='true' "
                                        + "data-toggle='tooltip' data-placement='left'"
                                        + "data-original-title='" + toolTipMessage + "'>"
                                        + "</i>"
                                }
                                return "<a href='#tasks' title='See Tasks' class='contractAgenda'>"
                                    + "<i class='fa fa-laptop fa-lg'></i>"
                                    + "</a>  "
                                    + "<a href='#updateContractCard' title='Edit Contract' class='editContract'>"
                                    + "<i class='fa fa-edit fa-lg'></i>"
                                    + "</a>  "
                                    + removeRestoreIcon;
                            } else {
                                return data;
                            }
                        }
                    }
                ],
                drawCallback: function () {
                    $('[data-toggle="tooltip"]').tooltip();
                    if ($("#contracts .contractAgenda").length > 0) {
                        $($("#contracts .contractAgenda")[0]).trigger("click");
                    }
                }
            });
        }

        $("#contracts ").on("click", ".contractAgenda", function () {
            var repo = $("#owner").text() + "/" + $("#name").text();
            var contributor = $(this).parent().parent().children()[0].innerText;
            var role = $(this).parent().parent().children()[1].innerText;
            var hourlyRate = $(this).parent().parent().children()[2].innerText;
            var provider = project.provider;
            var contract = {
                id: {
                    repoFullName: repo,
                    contributorUsername: contributor,
                    role: role,
                    provider: provider
                },
                hourlyRate: hourlyRate
            }
            getTasksOfContract(contract);
            getInvoicesOfContract(contract);
        });

        $("#contracts ").on("click", ".editContract", function () {
            var contributor = $(this).parent().parent().children()[0].innerText;
            var role = $(this).parent().parent().children()[1].innerText;

            $("#newContractCard").hide();

            $("#updateContractUsername").val(contributor);
            $("#updateContractRole").val(role);
            $("#usernameDisplayed").html(contributor);
            $("#roleDisplayed").html(role);
            $("#updatedHourlyRate").val("");

            $("#updateContractCard").show();
        });

        $("#contracts ").on("click", ".removeContract", function (event) {
            event.preventDefault();
            confirmDialog
                .create("Are you sure you want to remove this contract?", "Warning", "Yes")
                .then(() => markContractForRemoval($(this)));
        });

        $("#contracts ").on("click", ".restoreContract", function (event) {
            event.preventDefault();
            confirmDialog
                .create("Are you sure you want to restore this contract?", "Warning", "Yes")
                .then(() => restoreContract($(this)));
        });

        $("#projectContractsButton").click(
            () => {
                if ($("#projectContractsButton").hasClass("active")) {
                    return;
                }
                loadContracts();
                $("#projectContractsButton").addClass("active");
                $("#projectContracts").addClass("show");

                $("#projectTasksButton").removeClass("active");
                $("#projectOverviewButton").removeClass("active");
                $("#projectWalletsButton").removeClass("active");
                $("#projectSettingsButton").removeClass("active");
                
                $("#projectTasks").removeClass("show");
                $("#projectOverview").removeClass("show");
                $("#projectWallets").removeClass("show");
                $("#projectSettings").removeClass("show");

                //otherwise when tab is showing, table headers will be
                //initially squashed at start
                $($.fn.dataTable.tables(true)).DataTable().columns.adjust();
            }
        );

        $("#updateContractForm").submit(
            function(e) {
                e.preventDefault();
                var valid = true;
                $.each($("#updateContractForm .required"), function(index, element) {
                    if($(element).val() == '') {
                        $(element).addClass("is-invalid");
                        valid = valid && false;
                    } else {
                        $(element).removeClass("is-invalid");
                        valid = valid && true;
                    }
                });
                if(valid) {
                    $("#updateContractButton").addClass("disabled");
                    $("#cancelUpdateContract").addClass("disabled");
                    $("#loadingUpdateContract").show();
                    var formData = $(this).serialize();
                    //check if username exists before submit
                    contractsService.update(project, formData)
                        .then(
                            function (updatedContract) {
                                $("#updateContractForm input").val('');
                                $("#updateContractCard").hide();
                                $("#newContractCard").show();
                                var table = $("#contracts").DataTable();
                                var row = table.row("#" + updatedContract.id.contributorUsername + updatedContract.id.role);
                                table.cell({ row: row.index(), column: 2}).data(updatedContract.hourlyRate).draw("page")
                            }
                        ).catch(handleError)
                        .finally(
                            function(){
                                $("#updateContractButton").removeClass("disabled");
                                $("#cancelUpdateContract").removeClass("disabled");
                                $("#loadingUpdateContract").hide();
                            }
                        );
                    return false;
                }
            }
        );

        $("#cancelUpdateContract").on(
            "click",
            function(event) {
                event.preventDefault();

                $("#updateContractCard").hide();
                $("#newContractCard").show();

                $("#updateContractUsername").val("");
                $("#updateContractRole").val("");
                $("#usernameDisplayed").html("");
                $("#roleDisplayed").html("");
                $("#updatedHourlyRate").val("");
                $.each($("#updateContractForm .required"), function(index, element) {
                    $(element).removeClass("is-invalid");
                });

            }
        )

        /**
         * Mark a Contract for deletion.
         * @param button Remove Button.
         */
        function markContractForRemoval(button) {
            var repo = $("#owner").text() + "/" + $("#name").text();
            var table = $("#contracts").DataTable();
            var row = table.row(button.parents('tr')); // row of the button
            var data = row.data();
            $.ajax( //API call to get the active Invoice.
                "/api/projects/"
                + repo
                + "/contracts/" + data.contributorUsername + "/mark?role=" + data.role,
                {
                    type: "PUT",
                    success: function (contract) {
                        table.cell({ row: row.index(), column: 4 })
                            .data({ markedForRemoval: contract.markedForRemoval })
                            .draw("page");
                    },
                    error: function () {
                        alert("Something went wrong. Please refresh the page and try again.")
                    }
                }
            );
        }

        /**
         * Restore a contract.
         * @param contract Contract.
         * @param button Remove button.
         */
        function restoreContract(button) {
            var repo = $("#owner").text() + "/" + $("#name").text();
            var table = $("#contracts").DataTable();
            var row = table.row(button.parents('tr')); // row of the button
            var data = row.data()
            $.ajax( //API call to get the active Invoice.
                "/api/projects/"
                + repo
                + "/contracts/" + data.contributorUsername + "/mark?role=" + data.role,
                {
                    type: "DELETE",
                    success: function () {
                        table.cell({ row: row.index(), column: 4 })
                            .data({ markedForRemoval: 'null' });
                    },
                    error: function () {
                        alert("Something went wrong. Please refresh the page and try again.")
                    }
                }
            );
        }

        $("#addContractForm").submit(
            function(e) {
                e.preventDefault();
                var valid = true;
                $.each($("#addContractForm .required"), function(index, element) {
                    if($(element).val() == '') {
                        $(element).addClass("is-invalid");
                        valid = valid && false;
                    } else {
                        $(element).removeClass("is-invalid");
                        valid = valid && true;
                    }
                });
                if(valid) {
                    var formData = $(this).serialize();
                    // check if a contract exist with the same username and role.
                    const rowId = $("#username").val() + $("#role").val()
                    const exists = $("#contracts").DataTable().row("#"+rowId).length > 0;
                    if(exists) {
                        alert("Error: Can't add a same contract for the user: "
                            + $("#username").val() + " with role: " + $("#role").val())
                        return
                    }
                    //check if username exists before submit
                    usersService.exists(
                        $("#username").val(),
                        project.provider,
                        function(){
                            $("#addContractLoading").show();
                            clearFormErrors();
                            disableForm(true);
                        }
                    ).then(
                        function () {
                            return contractsService.add(project, formData)
                        }
                    ).then(
                        function (contract) {
                            $("#addContractForm input").val('');
                            $('#addContractForm option:first').prop('selected', true);
                            $("#contracts").DataTable()
                                .rows
                                .add([contractAsTableRowArray(contract)])
                                .draw()
                        }
                    ).catch(handleError)
                        .finally(
                            function(){
                            disableForm(false);
                            $("#addContractLoading").hide();
                        });
                    return false;
                }
            }
        );

        //autocomplete
        var debounce = null;
        $("#username").autocomplete({
            minChars: 3,
            triggerSelectOnValidInput: false,
            orientation: top,
            maxHeight: 100,
            onSearchStart: function(){
                $(".username-error").hide();
            },
            lookup: function(query, done){
                 console.log(query)
                 clearTimeout(debounce);
                 debounce = setTimeout(function() {
                    usersService
                        .findUsers(query, project.provider)
                        .then(function(users){
                            done({
                                suggestions: users.map(function(user){
                                    return {value: user, data: user };
                                })
                            });
                        })
                        .catch(handleError)
                 }, 500)
            },
            onSelect: function(suggestion){
                $("#username").val(suggestion.value)
            }
        });

    });

})(jQuery, contractsService, usersService, confirmDialog)
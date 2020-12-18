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
(function getAndAddContracts($, contractsService, usersService, confirmDialog){

    function getTasksOfContract(contract) {
        $("#tasksTable").dataTable().fnDestroy();
        $("#tasksTable").find("tbody").html('');
        $("#tasksTitle").html(
            " Tasks assigned to " + contract.id.contributorUsername
            + " (" + contract.id.role + ")"
        )
        $("#loadingTasks").show();
        $.ajax( //API call to get the Tasks.
            "/api/projects/"
            + contract.id.repoFullName
            + "/contracts/" + contract.id.contributorUsername + "/tasks?role=" + contract.id.role,
            {
                type: "GET",
                statusCode: {
                    200: function (tasks) {
                        $("#loadingTasks").hide();
                        tasks.forEach(
                            function(task) {
                                $("#tasksTable").find("tbody").append(
                                    taskAsTableRow(contract, task)
                                );
                            }
                        );
                        $("#tasksTable").dataTable();
                        $("#tasksBody").show();
                        console.log(tasks);
                    },
                    204: function (data) {
                        $("#loadingTasks").hide();
                        $("#loadingTasksHidden").show();
                        $("#tasksTable").dataTable();
                        $("#tasksBody").show();
                    },

                }
            }
        );
    }

    /**
     * Turn a Task into a table row.
     * @param task Task.
     */
    function taskAsTableRow(contract, task) {
        var issueLink;
        if(contract.id.provider == 'github') {
            issueLink = 'https://github.com/'
                + contract.id.repoFullName
                + "/issues/"
                + task.issueId;
        } else if(contract.id.provider == 'gitlab') {
            issueLink = 'https://gitlab.com/'
                + contract.id.repoFullName
                + "/issues/"
                + task.issueId;
        } else {
            issueLink = '#';
        }
        return "<tr>" +
            "<td><a href='" + issueLink + "' target='_blank'>#" + task.issueId + "</a></td>" +
            "<td>" + task.assignmentDate.split('T')[0] + "</td>"  +
            "<td>" + task.deadline.split('T')[0] + "</td>" +
            "<td>" + task.estimation + " min</td>" +
            "<td>" + formatEuro(task.value) + "</td>" +
            "</tr>"
    }

    function getInvoicesOfContract(contract) {
        $("#invoicesTable").dataTable().fnDestroy();
        $("#invoicesTable").find("tbody").html('');
        $("#invoicesTitle").html(
            " Invoices of " + contract.id.contributorUsername
            + " (" + contract.id.role + ")"
        )
        $("#loadingInvoices").show();
        $.ajax( //API call to get the Tasks.
            "/api/projects/"
            + contract.id.repoFullName
            + "/contracts/" + contract.id.contributorUsername
            + "/invoices?role=" + contract.id.role,
            {
                type: "GET",
                statusCode: {
                    200: function (invoices) {
                        $("#loadingInvoices").hide();
                        invoices.forEach(
                            function(invoice, index) {
                                $("#invoicesTable").find("tbody").append(
                                    invoiceAsTableRow(contract, invoice)
                                );
                                $($(".downloadInvoice")[$(".downloadInvoice").length -1]).on(
                                    "click",
                                    function(event) {
                                        event.preventDefault();
                                        invoiceToPdf(
                                            "/api/projects/"
                                            + contract.id.repoFullName
                                            + "/contracts/" + contract.id.contributorUsername + "/invoices/"
                                            + invoice.id
                                            + "?role=" + contract.id.role,
                                            invoice,
                                            contract
                                        );
                                    }
                                );
                                if(invoice.paymentTime == "null" && invoice.transactionId == "null" && parseFloat(invoice.totalAmount) > 0.0) {
                                    $($(".payInvoice")[$(".payInvoice").length -1]).on(
                                        "click",
                                        function(event) {
                                            event.preventDefault();
                                            var message = "Are you sure you want to make this payment?"
                                            if(activeWallet.type == 'FAKE') {
                                                message += ' You are using a fake wallet, the payment will be fictive.'
                                            }
                                            confirmDialog
                                                .create(message)
                                                .then(
                                                    () => payInvoice(invoice, contract, $(this))
                                                );
                                        }
                                    );
                                }
                            }
                        );
                        $("#invoicesTable").dataTable();
                        $("#invoicesBody").show();
                    },
                    204: function (data) {
                        $("#loadingInvoices").hide();
                        $("#invoicesTable").dataTable();
                        $("#invoicesBody").show();
                    },

                }
            }
        );
    }

    /**
     * Turn an Invoice into a table row.
     * @param invoice Invoice.
     */
    function invoiceAsTableRow(contract, invoice) {
        var status;
        var payIcon = "";
        var downloadLink = "<a href='#' title='Download Invoice' class='downloadInvoice'>"
            + "<i class='fa fa-file-pdf-o fa-lg'></i>"
            + "</a>  ";
        if (invoice.paymentTime == "null" && invoice.transactionId == "null") {
            status = "Active";
            if (parseFloat(invoice.totalAmount) > 0.0) {
                payIcon = "<a href='#' title='Pay Invoice' class='payInvoice'>"
                    + "<i class='fa fa-credit-card fa-lg'></i>"
                    + "</a>";
            }
        } else {
            status = "Paid";
        }
        return "<tr>" +
            "<td>" + invoice.id + "</td>" +
            "<td>" + invoice.createdAt.split('T')[0] + "</td>"  +
            "<td>" + invoice.totalAmount + "</td>" +
            "<td>" + status + "</td>" +
            "<td>"
            + downloadLink
            + payIcon
            + "</td>" +
            "</tr>"
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
     * Show loading indicator in a data table.
     * @param {String} tableId Table id (ex: #myTable).
     * @param {Number} [size = 100] Optional loading icon size in pixels.
     * @param {String} [path = /images/loading.svg] Optional path of loading icon. 
     */
    function dataTableShowLoading(tableId, size, path){
        var size = size || 100
        var path = path || '/images/loading.svg'
        $(tableId+' > tbody').html(
            '<tr class="odd" style= "height:'+ size +'px">' 
                + '<td valign="center" colspan="6" class="dataTables_empty">' 
                   + '<img src="'+ path +'" height=' + size +'px">'
                +'</td>'
            +'</tr>'
        );
    }

    $(document).ready(function(){

         var project = {
            owner: $("#owner").text(),
            name: $("#name").text()
         }

    
        function loadContracts() {
            $("#contracts").dataTable().fnDestroy();
            $("#contracts").dataTable({
                //setup
                serverSide:  true,
                searching:   false,//searching not possible yet on server
                ordering:    false,//same for ordering
                //adapt DataTable request to Self-Paged API specification.
                ajax: function(data, callback){
                    dataTableShowLoading("#contracts");
                   
                    var draw = data.draw; // draw counter that ensure the page draw ordering is respected
                    var page = {
                        no: Math.ceil(data.start/data.length) + 1,
                        size: data.length
                    }
                    //fetch the page from server
                    contractsService
                        .getAll(project, page)
                        .then(function(contracts){
                            //convert contracts page to DataTable "page" specification
                            var total = contracts.paged.totalPages * contracts.paged.current.size;
                            var dataTablePage = {
                                draw: draw,
                                recordsTotal: total,
                                recordsFiltered: total,
                                data: contracts.data.map(function(c){
                                    var removeRestoreIcon;
                                    if(c.markedForRemoval == 'null') {
                                        removeRestoreIcon = "<a href='#' title='Mark Contract For Removal' class='removeContract'>"
                                            +"<i class='fa fa-trash fa-lg'></i>"
                                            +"</a>";
                                    } else {
                                        var toolTipMessage = "This contract has been marked for removal on "
                                        + c.markedForRemoval.split('T')[0] + ". "
                                        + "No more tasks will be assigned to it and it will be removed after 30 days.";
                                        removeRestoreIcon = "<a href='#' title='Restore Contract' class='restoreContract'>"
                                            +"<i class='fa fa-recycle fa-lg'></i>"
                                            +"</a>  "
                                            +"<i class='fa fa-exclamation-circle fa-lg fakeWalletInfo' style='color:red;' aria-hidden='true' "
                                            + "data-toggle='tooltip' data-placement='left'"
                                            + "data-original-title='" + toolTipMessage + "'>"
                                            +"</i>"
                                    }
                                    return [
                                        c.id.contributorUsername,
                                        c.id.role,
                                        c.hourlyRate,
                                        c.value,
                                        "<a href='#tasks' title='See Tasks' class='contractAgenda'>"
                                        +"<i class='fa fa-laptop fa-lg'></i>"
                                        +"</a>  "
                                        +"<a href='#updateContractCard' title='Edit Contract' class='editContract'>"
                                        +"<i class='fa fa-edit fa-lg'></i>"
                                        +"</a>  "
                                        + removeRestoreIcon
                                    ];
                                })
                            };
                            //send page to DataTable to be rendered
                            callback(dataTablePage);
                            $('[data-toggle="tooltip"]').tooltip();
                            $(".contractAgenda").each(
                                function() {
                                    $(this).on(
                                        "click",
                                        function(event) {
                                            var repo = $("#owner").text() + "/" + $("#name").text();
                                            var contributor = $(this).parent().parent().children()[0].innerText;
                                            var role = $(this).parent().parent().children()[1].innerText;
                                            var hourlyRate = $(this).parent().parent().children()[2].innerText;
                                            var provider = "github";
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
                                        }
                                    )
                                }
                            );
                            if($(".contractAgenda").length > 0) {
                                $($(".contractAgenda")[0]).trigger("click");
                            }
                            $(".editContract").each(
                                function() {
                                    $(this).on(
                                        "click",
                                        function(event) {
                                            var contributor = $(this).parent().parent().children()[0].innerText;
                                            var role = $(this).parent().parent().children()[1].innerText;

                                            $("#newContractCard").hide();

                                            $("#updateContractUsername").val(contributor);
                                            $("#updateContractRole").val(role);
                                            $("#usernameDisplayed").html(contributor);
                                            $("#roleDisplayed").html(role);
                                            $("#updatedHourlyRate").val("");

                                            $("#updateContractCard").show();
                                        }
                                    )
                                }
                            );
                            $(".removeContract").each(
                                function() {
                                    $(this).on(
                                        "click",
                                        function(event) {
                                            event.preventDefault();

                                            var repo = $("#owner").text() + "/" + $("#name").text();
                                            var contributor = $(this).parent().parent().children()[0].innerText;
                                            var role = $(this).parent().parent().children()[1].innerText;
                                            var provider = "github";
                                            var contract = {
                                                id: {
                                                    repoFullName: repo,
                                                    contributorUsername: contributor,
                                                    role: role,
                                                    provider: provider
                                                }
                                            }
                                            confirmDialog
                                                .create("Are you sure you want to remove this contract?")
                                                .then(() => markContractForRemoval(contract));
                                        }
                                    )
                                }
                            )
                            $(".restoreContract").each(
                                function() {
                                    $(this).on(
                                        "click",
                                        function(event) {
                                            event.preventDefault();

                                            var repo = $("#owner").text() + "/" + $("#name").text();
                                            var contributor = $(this).parent().parent().children()[0].innerText;
                                            var role = $(this).parent().parent().children()[1].innerText;
                                            var provider = "github";
                                            var contract = {
                                                id: {
                                                    repoFullName: repo,
                                                    contributorUsername: contributor,
                                                    role: role,
                                                    provider: provider
                                                }
                                            }
                                            confirmDialog
                                                .create("Are you sure you want to restore this contract?")
                                                .then(() => restoreContract(contract));
                                        }
                                    )
                                }
                            )
                        })
                        .catch(handleError);
                }
            });
        }

        $("#projectContractsButton").click(
            ()=> {
                if($("#projectContractsButton").hasClass("active")) {
                    return;
                }
                loadContracts();
                $("#projectContractsButton").addClass("active");
                $("#projectContracts").addClass("show");

                $("#projectOverviewButton").removeClass("active");
                $("#projectWalletsButton").removeClass("active");
                $("#projectOverview").removeClass("show");
                $("#projectWallets").removeClass("show");
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
                            function(updatedContract){
                                $("#updateContractForm input").val('');
                                loadContracts();
                                $("#updateContractCard").hide();
                                $("#newContractCard").show();
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
         * @param contract Contract.
         */
        function markContractForRemoval(contract) {
            $.ajax( //API call to get the active Invoice.
                "/api/projects/"
                + contract.id.repoFullName
                + "/contracts/" + contract.id.contributorUsername + "/mark?role=" + contract.id.role,
                {
                    type: "PUT",
                    success: function() {
                        loadContracts();
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
         */
        function restoreContract(contract) {
            $.ajax( //API call to get the active Invoice.
                "/api/projects/"
                + contract.id.repoFullName
                + "/contracts/" + contract.id.contributorUsername + "/mark?role=" + contract.id.role,
                {
                    type: "DELETE",
                    success: function() {
                        loadContracts();
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
                    //check if username exists before submit
                    usersService.exists(
                        $("#username").val(),
                        "github",
                        function(){
                            $("#addContractLoading").show();
                            clearFormErrors();
                            disableForm(true);
                            }
                        ).then(
                            function(){
                                return contractsService.add(project, formData)
                            }
                        ).then(
                            function(contract){
                                $("#addContractForm input").val('');
                                $('#addContractForm option:first').prop('selected',true);
                                //we check the current page (0 based) displayed in table.
                                //if is last page, we're adding the contract to table.
                                //since it's the latest contract created.
                                loadContracts();
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
                        .findUsers(query, "github")
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
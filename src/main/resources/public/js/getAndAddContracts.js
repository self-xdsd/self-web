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
        console.log("GET TASKS:");
        console.log(contract);
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
            "<td>" + task.assignmentDate + "</td>"  +
            "<td>" + task.deadline + "</td>" +
            "<td>" + task.estimation + "min</td>" +
            "</tr>"
    }

    function activeInvoiceToPdf(contract) {
        $.ajax( //API call to get the active Invoice.
            "/api/projects/"
            + contract.id.repoFullName
            + "/contracts/" + contract.id.contributorUsername + "/invoice?role=" + contract.id.role,
            {
                type: "GET",
                statusCode: {
                    200: function (fullInvoice) {
                        console.log("INVOICE: ");
                        console.log(fullInvoice);
                        window.jsPDF = window.jspdf.jsPDF; // add this line of code

                        const doc = new jsPDF();

                        doc.addImage("/images/self-xdsd.png", "png", 170, 10, 20, 20);
                        doc.text("Invoice", 105, 20, null, null, "center");
                        doc.text("Invoice #" + fullInvoice.id + " from " + fullInvoice.createdAt, 20, 35);
                        doc.text("Contributor: " + contract.id.contributorUsername, 20, 42);
                        doc.text("Project: " + contract.id.repoFullName + " at " + contract.id.provider, 20, 49);
                        doc.text("Role: " + contract.id.role, 20, 56);
                        doc.text("Hourly Rate: " + contract.hourlyRate.replace("€", "EUR"), 20, 63);
                        doc.text("______________________________", 20, 67);

                        doc.text("Total Due: " + fullInvoice.totalAmount.replace("€", "EUR"), 20, 74);
                        var toContributor = function() {
                            var toContributor = 0;
                            fullInvoice.tasks.forEach(
                                function(task){
                                    toContributor += task.value
                                }
                            )
                            return toContributor;
                        }.call();
                        doc.text("to Contributor: " + toContributor + " EUR", 23, 81);
                        var toPm = function() {
                            var toPm = 0;
                            fullInvoice.tasks.forEach(
                                function (task) {
                                    toPm += task.commission
                                }
                            )
                            return toPm;
                        }.call();
                        doc.text("to Project Manager: " + toPm + " EUR", 23, 88);
                        if(fullInvoice.isPaid) {
                            doc.text("Status: paid", 20, 95)
                            doc.text("paid at: " + fullInvoice.paymentTime, 23, 102)
                            doc.text("transaction id: " + fullInvoice.transactionId, 23, 109)
                        } else {
                            doc.text("Status: active (not paid)", 20, 95)
                            doc.text("payment due: next Monday", 23, 102)
                        }
                        doc.text("Invoiced tasks bellow.", 20, 116);

                        doc.text("______________________________", 20, 120);

                        function createHeaders(keys) {
                            var result = [];
                            for (var i = 0; i < keys.length; i += 1) {
                                result.push({
                                    id: keys[i],
                                    name: keys[i],
                                    prompt: keys[i],
                                    width: 30,
                                    align: "center",
                                    padding: 0
                                });
                            }
                            return result;
                        }

                        var headers = createHeaders([
                            "Issue ID",
                            "Estimation",
                            "Value",
                            "Commission"
                        ]);

                        var generateData = function(invoicedTasks) {
                            var result = [];
                            for (var i = 0; i < invoicedTasks.length; i += 1) {
                                var task = {
                                    "Issue ID": invoicedTasks[i].issueId,
                                    Estimation: invoicedTasks[i].estimation + "min",
                                    Value: invoicedTasks[i].value + " EUR",
                                    Commission: invoicedTasks[i].commission + " EUR"
                                };
                                result.push(Object.assign({}, task));
                            }
                            return result;
                        };
                        doc.table(
                            55, 127,
                            generateData(fullInvoice.tasks),
                            headers,
                            {
                                autoSize: true
                            }
                        );

                        doc.save("self_invoice_" + fullInvoice.id + ".pdf");
                    }
                }
            }
        );
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

    $(document).ready(function(){

         var project = {
            owner: $("#owner").text(),
            name: $("#name").text()
         }

        //while there is still something loading, keep showing the loading animation.
        //increments when there is a remote request ongoing (fetch a page, submit new contract)
        //decrements when a request is done (even with an error).
        //if reaches '0' the loading indicator will hide.
        var loadingQueue = 0;
        function showLoading(){
            if(loadingQueue++ === 0){
                $("#loadingContracts").show();
            }
        }
        function hideLoading(){
            if(--loadingQueue === 0){
                 $("#loadingContracts").hide();
            }
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
                    var draw = data.draw; // draw counter that ensure the page draw ordering is respected
                    var page = {
                        no: Math.ceil(data.start/data.length) + 1,
                        size: data.length
                    }
                    //fetch the page from server
                    contractsService
                        .getAll(project, page, showLoading)
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
                                        var toolTipMessage = "This contract has been marked for removal on " + c.markedForRemoval + ". "
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
                                        + "<a href='#' title='Download Invoice' class='downloadInvoice'>"
                                        +"<i class='fa fa-file-pdf-o fa-lg'></i>"
                                        +"</a>  "
                                        +"<a href='#' title='Pay Invoice' class='payContract'>"
                                        +"<i class='fa fa-euro fa-lg'></i>"
                                        +"</a>  "
                                        +"<a href='#' title='Edit Contract' class='editContract'>"
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
                                            var provider = "github";
                                            var contract = {
                                                id: {
                                                    repoFullName: repo,
                                                    contributorUsername: contributor,
                                                    role: role,
                                                    provider: provider
                                                }
                                            }
                                            getTasksOfContract(contract);
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
                                            event.preventDefault();
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
                            $(".downloadInvoice").each(
                                function() {
                                    $(this).on(
                                        "click",
                                        function(event) {
                                            event.preventDefault();

                                            var repo = $("#owner").text() + "/" + $("#name").text();
                                            var contributor = $(this).parent().parent().children()[0].innerText;
                                            var role = $(this).parent().parent().children()[1].innerText;
                                            var hourly = $(this).parent().parent().children()[2].innerText;
                                            var provider = "github";
                                            var contract = {
                                                id: {
                                                    repoFullName: repo,
                                                    contributorUsername: contributor,
                                                    role: role,
                                                    provider: provider
                                                },
                                                hourlyRate: hourly
                                            }
                                            activeInvoiceToPdf(contract);
                                        }
                                    )
                                }
                            );
                            $(".payContract").each(
                                function() {
                                    $(this).on(
                                        "click",
                                        function(event) {
                                            event.preventDefault();

                                            var repo = $("#owner").text() + "/" + $("#name").text();
                                            var contributor = $(this).parent().parent().children()[0].innerText;
                                            var role = $(this).parent().parent().children()[1].innerText;
                                            var hourly = $(this).parent().parent().children()[2].innerText;
                                            var provider = "github";
                                            var contract = {
                                                id: {
                                                    repoFullName: repo,
                                                    contributorUsername: contributor,
                                                    role: role,
                                                    provider: provider
                                                },
                                                hourlyRate: hourly
                                            }
                                            payContract(repo, contract);
                                        }
                                    )
                                }
                            );
                        })
                        .catch(handleError)
                        .finally(hideLoading);
                }
            });
        }

        loadContracts();

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
            lookup: function(query, done){
                 console.log(query)
                 clearTimeout(debounce);
                 debounce = setTimeout(function() {
                    usersService
                        .findUsers(query, "github", showLoading)
                        .then(function(users){
                            done({
                                suggestions: users.map(function(user){
                                    return {value: user, data: user };
                                })
                            });
                        })
                        .catch(handleError)
                        .finally(hideLoading);
                 }, 500)
            },
            onSelect: function(suggestion){
                $("#username").val(suggestion.value)
            }
        });

    });

})(jQuery, contractsService, usersService, confirmDialog)
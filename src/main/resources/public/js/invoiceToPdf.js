/**
 * Get the whole Invoice from the server and download it as PDF.
 * @param invoice Partial invoice (it doesn't have the tasks).
 * @param contract Contract to which the Invoice belongs.
 * @param invoiceUri Invoice URI.
 */
function invoiceToPdf(invoiceUri, invoice, contract) {
    window.jsPDF = window.jspdf.jsPDF;
    $.ajax(
        invoiceUri,
        {
            type: "GET",
            statusCode: {
                200: function (fullInvoice) {
                    const doc = new jsPDF();

                    doc.addImage("/images/self-xdsd.png", "png", 170, 10, 20, 20);
                    doc.text("Invoice", 105, 20, null, null, "center");
                    doc.text("Invoice #" + fullInvoice.id + " from " + fullInvoice.createdAt.split('T')[0], 20, 35);
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
                    doc.text("to Contributor: " + formatEuro(toContributor).replace("€", "EUR"), 23, 81);
                    var toPm = function() {
                        var toPm = 0;
                        fullInvoice.tasks.forEach(
                            function (task) {
                                toPm += task.commission
                            }
                        )
                        return toPm;
                    }.call();
                    doc.text("to Project Manager: " + formatEuro(toPm).replace("€", "EUR"), 23, 88);
                    if(fullInvoice.isPaid) {
                        doc.text("Status: paid", 20, 95)
                        doc.text("paid at: " + fullInvoice.paymentTime.split('T')[0], 23, 102)
                        doc.text("transaction id: " + fullInvoice.transactionId, 23, 109)
                    } else {
                        doc.text("Status: active (not paid)", 20, 95)
                        doc.text("payment due: next Monday.", 23, 102)
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
                                Estimation: invoicedTasks[i].estimation + " min",
                                Value: formatEuro(invoicedTasks[i].value).replace("€", "EUR"),
                                Commission: formatEuro(invoicedTasks[i].commission).replace("€", "EUR")
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
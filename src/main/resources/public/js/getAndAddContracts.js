(function getAndAddContracts($, service){

    function addContractToTable(contract){
        var row = "<tr>"
                    +"<td width='25%'>"+contract.id.contributorUsername+"</td>"
                    +"<td width='10%'>"+contract.id.role+"</td>"
                    +"<td width='15%'>"+contract.hourlyRate+"</td>"
                    +"<td width='15%'>"+contract.value+"</td>"
                    +"<td>"+contract.project.manager.username+"</td>"
                    +"<td width='7%' style='text-align:center'>"+contract.project.manager.commission+"</td>"
                  +"</tr>";
        $("#contracts").find("tbody").append(row);
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
        var loadingQueue = 0;

        $("#contracts").DataTable({
            serverSide: true,
            searching: false,
            ordering:  false,
            ajax: function(data, callback, settings){
                //save "data" in a closure achieved with a self invoked function.
                //this will guarantee that "data.draw" is in sync for each request (regardless of spam)
                //without having to send it to self-server and echo it back.
                (function(_data){
                    var draw = _data.draw;
                    var page = {
                        no: Math.floor(_data.start/_data.length) + 1,
                        size: _data.length
                    }
                    service
                        .getAll(project, page, function(){
                            $("#loadingContracts").show();
                            loadingQueue++;
                        })
                        .then(function(contracts){
                            var dataTablePage = {
                                draw: draw,
                                recordsTotal: contracts.page.totalRecords,
                                recordsFiltered: contracts.page.totalRecords,
                                data: contracts.data.map(function(c){
                                    return [
                                        c.id.contributorUsername,
                                        c.id.role,
                                        c.hourlyRate,
                                        c.value,
                                        c.project.manager.username
                                    ];
                                })
                            };
                            callback(dataTablePage);
                         })
                        .catch(handleError)
                        .finally(function(){
                            if(--loadingQueue === 0){
                                 $("#loadingContracts").hide();
                            };
                        });
                })(data);
            }
        });


        $("#addContractForm").submit(function(e){
            e.preventDefault();
            var form = $(this);
            service
                .add(project, form.serialize(), function(){
                    $("#loadingContracts").show();
                    clearFormErrors();
                    disableForm(true);
                    loadingQueue++;
                })
                .then(function(contract){
                    form.trigger('reset');
                    addContractToTable(contract);
                })
                .catch(handleError)
                .finally(function(){
                   disableForm(false);
                   if(--loadingQueue === 0){
                      $("#loadingContracts").hide();
                   }
                });
        })
    });
})(jQuery, contractsService)
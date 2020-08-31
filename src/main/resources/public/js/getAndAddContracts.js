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

        service
            .getAll(project, function(){
                $("#loadingContracts").show();
                loadingQueue++;
            })
            .then(function(contracts){ contracts.forEach(addContractToTable); })
            .catch(handleError)
            .finally(function(){
                if(--loadingQueue === 0){
                     $("#loadingContracts").hide();
                };
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
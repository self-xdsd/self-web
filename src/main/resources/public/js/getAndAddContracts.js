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
(function getAndAddContracts($, service){

    function addContractToTable(contract){
        var row = "<tr>"
                    +"<td  width='50%'>"+contract.id.contributorUsername+"</td>"
                    +"<td>"+contract.id.role+"</td>"
                    +"<td>"+contract.hourlyRate+"</td>"
                    +"<td>"+contract.value+"</td>"
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
        //increments when there is a remote request ongoing (fetch a page, submit new contract)
        //decrements when a request is done (even with an error).
        //if reaches '0' the loading indicator will hide.
        var loadingQueue = 0;

        var table = $("#contracts").DataTable({
            //setup
            serverSide:  true,
            searching:   false,//searching not possible yet on server
            ordering:    false,//same for ordering
            fixedHeader: true,
            scrollY:    '50vh',
            //adapt DataTable request to Self-Paged API specification.
            ajax: function(data, callback, settings){
                var draw = data.draw; // draw counter that ensure the page draw ordering is respected
                var page = {
                    no: Math.ceil(data.start/data.length) + 1,
                    size: data.length
                }
                //fetch the page from server
                service
                    .getAll(project, page, function(){
                        //if there is no loading on screen, show it
                        if(loadingQueue++ === 0){
                            $("#loadingContracts").show();
                        }
                    })
                    .then(function(contracts){
                        //convert contracts page to DataTable "page" specification
                        var total = contracts.paged.totalPages * contracts.paged.current.size;
                        var dataTablePage = {
                            draw: draw,
                            recordsTotal: total,
                            recordsFiltered: total,
                            data: contracts.data.map(function(c){
                                return [
                                    c.id.contributorUsername,
                                    c.id.role,
                                    c.hourlyRate,
                                    c.value
                                ];
                            })
                        };
                        //send page to DataTable to be rendered
                        callback(dataTablePage);
                     })
                    .catch(handleError)
                    .finally(function(){
                        if(--loadingQueue === 0){
                             $("#loadingContracts").hide();
                        };
                    });
            }
        });


        $("#addContractForm").submit(function(e){
            e.preventDefault();
            var form = $(this);
            service
                .add(project, form.serialize(), function(){
                    if(loadingQueue++ === 0){
                       $("#loadingContracts").show();
                    }
                    clearFormErrors();
                    disableForm(true);
                })
                .then(function(contract){
                    form.trigger('reset');
                    //we check the current page (0 based) displayed in table.
                    //if is last page, we're adding the contract to table.
                    //since it's the latest contract created.
                    var pageInfo = table.page.info();
                    if((pageInfo.page + 1) === pageInfo.pages){
                        addContractToTable(contract);
                    }

                })
                .catch(handleError)
                .finally(function(){
                   disableForm(false);
                   if(--loadingQueue === 0){
                      $("#loadingContracts").hide();
                   }
                });
        });
    });
})(jQuery, contractsService)
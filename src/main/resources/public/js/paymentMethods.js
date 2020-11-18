/**
 * Functions handling a wallet's PaymentMethods
 * @param repoOwner Owner of the repository/project.
 * @param repoName Name of the repository.
 * @param method The PaymentMethod to be activate.
 */
function activatePaymentMethod(repoOwner, repoName, method){
    $.ajax(
        {
            type: "PUT",
            contentType: "application/json",
            url: "/api/projects/" + repoOwner + "/" + repoName +
                "/wallets/stripe/paymentMethods/" + method.self.paymentMethodId + "/activate",
            success: function (activated) {
                //nothing to do
            },
            error: function(jqXHR, error, errorThrown) {
                console.log("Server error status: " + jqXHR.status);
                console.log("Server error: " + jqXHR.responseText);
                alert(
                    "Something went wrong (" + jqXHR.status + ")." +
                    "Please, refresh the page and try again."
                );
            }
        }
    );
}
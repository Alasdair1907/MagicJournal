let  ajax = async function(data, errDescrDefault) {
    let adminResponseJson;
    try {
        adminResponseJson = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: data
        });
    } catch (error){
        console.log("helper.js ajax() - ajax error");
    }

    let adminResponse = adminResponseJson ? JSON.parse(adminResponseJson) : {data: null, success: false, errorDescription: errDescrDefault};

    if (adminResponse.success === false) {
        alert("ERROR: "+adminResponse.errorDescription);
        return undefined;
    }

    return adminResponse.data;
};

let buttonSpinner = "<i class=\"fas fa-cog fa-spin\"></i>";

let spinButton = function($buttonElem){
    let buttonText = $buttonElem.html();
    $buttonElem.html(buttonSpinner);
    $buttonElem.prop("disabled", true);

    return buttonText;
};

let unSpinButton = function($buttonElem, text){
    $buttonElem.html(text);
    $buttonElem.prop("disabled", false);
};

function isNumericOrEmpty(value) {
    if (value === null){
        return true;
    }

    if (value === ""){
        return true;
    }

    return new RegExp('^[0-9]+$').test(value);
}
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
    if (value === null || value === undefined){
        return true;
    }

    if (value === ""){
        return true;
    }

    return new RegExp('^[0-9]+$').test(value);
}

function checkCoordinates(coordinates){
    if (coordinates === null || coordinates === undefined || coordinates === ""){
        return true;
    }

    // ^\-?[0-9]{1,2}(\.[0-9]{1,})?\,\s+\-?[0-9]{1,3}(\.[0-9]{1,})?$
    let res = new RegExp("^\\-?[0-9]{1,2}(\\.[0-9]{1,})?\\,\\s+\\-?[0-9]{1,3}(\\.[0-9]{1,})?$").test(coordinates);

    if (!res){
        alert("Coordinates must be in the following format:\nlatitude, longtitude\nFor example:\n50.038384, 14.358546\n-33.438296, -70.650954\n51, -3");
        return false;
    }

    return true;

}

function isDemo(){
    if (Cookies.get("privilegeLevelName") === "demo"){
        return true;
    }

    return false;
}
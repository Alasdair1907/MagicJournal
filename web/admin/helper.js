var POST_ATTRIBUTION_GALLERY = 0;
var POST_ATTRIBUTION_PHOTO = 1;
var POST_ATTRIBUTION_ARTICLE = 2;
var POST_ATTRIBUTION_PROFILE = 3;

let ajaxCda = async function(data, errDescrDefault){
    return await ajax(data, errDescrDefault, true);
};

let ajax = async function(data, errDescrDefault, client) {
    let adminResponseJson;
    let url;

    if (client){
        url = 'admin/jsonApi.jsp';
    } else {
        url = 'jsonApi.jsp';
    }

    try {
        adminResponseJson = await $.ajax({
            url: url,
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

let toBase64 = async function(input){
    return await ajax({action: "toBase64Utf8", data: input});
};

let fromBase64 = async function(input){
    return await ajax({action: "fromBase64Utf8", data: input});
};

let insertAtPosition = function($textarea, cursorPos, toInsert){
    let text = $textarea.val();
    let textBefore = text.substring(0,  cursorPos);
    let textAfter  = text.substring(cursorPos, text.length);

    $textarea.val(textBefore + toInsert + textAfter);
    $textarea.prop('selectionEnd', (cursorPos+toInsert.length) );
    $textarea.focus();

};
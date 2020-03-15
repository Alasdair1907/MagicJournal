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
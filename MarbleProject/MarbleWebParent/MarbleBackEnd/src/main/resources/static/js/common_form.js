$(document).ready(function() {
    //   Click CANCEL Button return to /users
    $("#buttonCancel").on("click", function () {
        window.location = moduleURL;
    });
    // file image upload preview
    $("#fileImage").change(function () {
        fileSize = this.files[0].size;

        if (fileSize > 1048576) {
            this.setCustomValidity("You must choose an image less than 1MB!");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    })
});

// show the image preview
function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    };
    reader.readAsDataURL(file);
}
// show file name in the image upload
$('.custom-file-input').on('change', function() {
    let fileName = $(this).val().split('\\').pop();
    $(this).siblings('.custom-file-label').addClass("selected").html(fileName);
});
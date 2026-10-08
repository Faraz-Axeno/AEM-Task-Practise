document.addEventListener("DOMContentLoaded", function() {
    var buttons = document.querySelectorAll(".cmp-teamdetails__toggle-role");
    
    buttons.forEach(function(button) {
        button.addEventListener("click", function() {
            var memberCard = this.closest(".cmp-teamdetails__member");
            if (memberCard) {
                var roleElement = memberCard.querySelector(".cmp-teamdetails__role");
                if (roleElement) {
                    var isHidden = roleElement.style.opacity === "0" || roleElement.style.visibility === "hidden" || roleElement.style.display === "none";
                    if (isHidden) {
                        roleElement.style.opacity = "1";
                        roleElement.style.display = "block";
                        roleElement.style.visibility = "visible";
                        this.setAttribute("aria-expanded", "true");
                    } else {
                        roleElement.style.opacity = "0";
                        roleElement.style.display = "none";
                        roleElement.style.visibility = "hidden";
                        this.setAttribute("aria-expanded", "false");
                    }
                }
            }
        });
        
        // Accessibility
        button.setAttribute("aria-expanded", "true");
    });
});


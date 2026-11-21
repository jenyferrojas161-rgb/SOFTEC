function actualizarHora() {
    let fecha = new Date();

    document.getElementById("fechaHora").innerHTML =
        "📅 " + fecha.toLocaleString("es-CO");
}

setInterval(actualizarHora, 1000);
actualizarHora();


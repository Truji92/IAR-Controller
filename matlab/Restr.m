function [ z, zz ] = Restr(centroyradio, DatosColor, DatosFondo, porcentajePerdida, porcentajeFondo)
   
    % restriccion para color
    R = double(DatosColor(:,2));
    G = double(DatosColor(:,3));
    B = double(DatosColor(:,4));

    Rc = centroyradio(1); Gc = centroyradio(2); Bc = centroyradio(3);
    Radio = centroyradio(4);
    distancias = sqrt((R-Rc).^2 +(G-Gc).^2 + (B-Bc).^2);
    detectados = distancias <= Radio;
    n = sum(detectados(:));
    
    z(1) = 1 - n/length(R) - porcentajePerdida;
    zz = [];
    
    % restriccion para fondo
    R = double(DatosFondo(:,2));
    G = double(DatosFondo(:,3));
    B = double(DatosFondo(:,4));

    distancias = sqrt((R-Rc).^2 +(G-Gc).^2 + (B-Bc).^2);
    detectados = distancias <= Radio;
    n = sum(detectados(:));
    z(2) = n/length(R) - porcentajeFondo;
   
end


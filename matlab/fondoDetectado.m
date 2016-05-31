function [n] = fondoDetectado(centroyradio, DatosFondo)
    R = double(DatosFondo(:,2));
    G = double(DatosFondo(:,3));
    B = double(DatosFondo(:,4));

    Rc = centroyradio(1); Gc = centroyradio(2); Bc = centroyradio(3);
    Radio = centroyradio(4);
    distancias = sqrt((R-Rc).^2 +(G-Gc).^2 + (B-Bc).^2);
    detectados = distancias <= Radio;
    n = sum(detectados(:));
end
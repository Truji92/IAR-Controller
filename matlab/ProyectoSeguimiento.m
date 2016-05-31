clear;

%% EXTRACCI�N DE DATOS DE COLOR DE OBJETO Y FONDO

DatosColor = [];
for i=1:6
    nombre = strcat('images/objeto', num2str(i), '.jpg');
    I = imread(nombre); 
    ROI = roipoly(I);
    R = I(:,:,1);
    G = I(:,:,2);
    B = I(:,:,3);
    datos = [R(ROI), G(ROI), B(ROI)];
    indice = i * ones(size(datos(:,1)));
    DatosColor = [DatosColor; indice, datos];
end

DatosFondo = []

for i=1:3
    nombre = strcat('images/fondo', num2str(i), '.jpg');
    I = imread(nombre); 
    ROI = roipoly(I);
    R = I(:,:,1);
    G = I(:,:,2);
    B = I(:,:,3);
    datos = [R(ROI), G(ROI), B(ROI)];
    indice = i * ones(size(datos(:,1)));
    DatosFondo = [DatosFondo; indice, datos];
end
  
%% ANALISIS DE DATOS Y ELECCI�N DE ESTRATEGIA DE SEGUIMIENTO

plot3(DatosFondo(:,2), DatosFondo(:,3), DatosFondo(:,4), 'b*');
hold on;
plot3(DatosColor(:,2), DatosColor(:,3), DatosColor(:,4), 'r*');

R = double(DatosColor(:,2));
G = double(DatosColor(:,3));
B = double(DatosColor(:,4));

Rc = mean(R); Gc = mean(G); Bc = mean(B);
distancias = sqrt((R-Rc).^2 +(G-Gc).^2 + (B-Bc).^2);
sigma = std(distancias);
Radio = 4*sigma;

[R,G,B] = sphere(100);
% Matrices de puntos de una esfera 
% centrada en el origen de radio unidad
x = Radio*R(:)+Rc; y = Radio*G(:)+Gc; z = Radio*B(:)+Bc;
plot3(x,y,z, '.g')

%% CALIBRACI�N Y AJUSTE DE PAR�METROS
ImagenConObjetoMasPequeno = 6;
NumeroMinimoPixeles = sum(DatosColor(:,1) == ImagenConObjetoMasPequeno);

% Calibracion de centro y radio
 
VectorInicial = [Rc, Gc, Bc, Radio];
VectorInicial = double(VectorInicial);
[u, fu] = fminsearch(@(u) fondoDetectado(u, DatosFondo), VectorInicial);
% porcentajePerdidaPermitido = 0.2;
% porcentajeFondoPermitido = 0.1;
% [u, fu] = fmincon( @(u) fondoDetectado(u, double(DatosFondo)), VectorInicial, [],[],[],[],[],[], @(u) Restr(u, double(DatosColor), double(DatosFondo), porcentajePerdidaPermitido, porcentajeFondoPermitido));
[R,G,B] = sphere(100);
x = u(4)*R(:)+u(1); y = u(4)*G(:)+u(2); z = u(4)*B(:)+u(3);
plot3(x,y,z, '.y')

% Para Comparar detecciones

for i= 1:5
    nombre = strcat('images/objeto', num2str(i), '.jpg');
    I = double(imread(nombre)); 
    Candidatos1 = sqrt((I(:,:,1)-Rc).^2 +(I(:,:,2)-Gc).^2 + (I(:,:,3)-Bc).^2) < Radio;
    Candidatos2 = sqrt((I(:,:,1)-u(1)).^2 +(I(:,:,2)-u(2)).^2 + (I(:,:,3)-u(3)).^2) < u(4);
    subplot(1,2,1); funcion_visualiza(uint8(I), Candidatos1, [0,255,0]);
    subplot(1,2,2); funcion_visualiza(uint8(I), Candidatos2, [0,255,0]);
    
    prompt = 'Continuar ';
    input(prompt);
end


%Para Usar esfera de fondo m�nimo
Rc = u(1); Gc = u(2); Bc = u(3); Radio = u(4);

%% IMPLEMENTACI�N Y VISUALIZACI�N DEL ALGORITMO DE SEGUIMIENTO

for i= 1:5
    nombre = strcat('images/objeto', num2str(i), '.jpg');
    I = double(imread(nombre)); 
    imshow(uint8(I));
    Candidatos = sqrt((I(:,:,1)-Rc).^2 +(I(:,:,2)-Gc).^2 + (I(:,:,3)-Bc).^2) < Radio;
    
    [Ietiq, N]=bwlabel(Candidatos);
    if N > 0 
        stats=regionprops(Ietiq,'Area','Centroid'); 
        areas=cat(1,stats.Area);
        centroides=cat(1,stats.Centroid); 
        for j=1:N
           if areas(j) >= NumeroMinimoPixeles 
                hold on;
                plot(centroides(j,1), centroides(j,2), 'bx'); 
                hold off;
           end
        end
    end
    
    prompt = 'Continuar ';
    input(prompt);
end






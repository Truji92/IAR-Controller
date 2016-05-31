function [img ] = funcion_visualiza(I, Ib, color)
%FUNCION_VISUALIZA Summary of this function goes here
%   Detailed explanation goes here
    I = double(I);
    [x y z] = size(I);
    if (z == 1)
        I(:,:,2) = I(:,:,1);
        I(:,:,3) = I(:,:,1);
    end
    img = zeros(x,y,3);
    for i = [1:3]
        img(:,:,i) = (Ib ~= 1) .* I(:,:,i);
        img(:,:,i) = img(:,:,i) + Ib*color(i); 
    end
     
    imshow (uint8(img));

end


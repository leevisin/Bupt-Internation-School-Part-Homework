%第零步读入图片：
data = imread('Lenna.png');%导入图片
dataEn = entropy(data);%求熵
dataAve = mean2(data);%求均值
dataVar = var(double(data(:)));%求方差

%第一步：转换为灰度图像
greyData = rgb2gray(data);%图像灰度化

greyEn = entropy(greyData);%求熵
greyAve = mean2(greyData);%求均值
greyVar = var(double(greyData(:)));%求方差
greyDataF = fftshift(fft2(double(greyData)));%傅里叶变换
greyMagnitude = log(1+abs(greyDataF));%求幅度

figure(1);%画出灰度图的相关图像
subplot(2,2,4);
subplot(2,2,1);imshow(data);title('原图');
subplot(2,2,2);imshow(greyData);title('灰度图');
subplot(2,2,3);imshow(greyMagnitude,[]);title('灰度图的幅度谱');
subplot(2,2,4);imhist(data);title('灰度直方图');


%第二步：加高斯白噪声后用低通滤波器去除噪声
distData = imnoise(greyData, 'gaussian', 0, 10^2/255^2);%将均值为0，方差为10^2/255^2的高斯噪声加到图像greyData上
distEn = entropy(distData);%求熵
distAve = mean2(distData);%求均值
distVar = var(double(distData(:)));%求方差

%低通滤波
distDataF = fftshift(fft2(double(distData)));%傅里叶变换
[m, n] = size(distDataF);%获得图像大小
mMid = fix(m/2);%数据修正
nMid = fix(n/2);%数据修正
lpfData = zeros(m,n);%初始化
d0=50;%阈值 第一次为50 第二次为5
for i=1:m %边历图像像素
    for j=1:n
        d=sqrt((i -mMid)^2+(j-nMid)^2);%理想低通滤波，求距离
        if d<=d0
            h(i,j)=1; 
        else
            h(i,j)=0; %被低通滤波器滤走
        end
        lpfData(i,j)=h(i,j)*distDataF(i,j);  
    end
end %进行低通滤波  
filtData=ifftshift(lpfData);%反傅里叶变换
filtData=uint8(real(ifft2(filtData)));%取实数部分

filtEn = entropy(filtData);%求熵
filtAve = mean2(filtData);%求均值
filtVar = var(double(filtData(:)));%求方差

%画出滤波前后的对比图像
figure(2);
subplot(1,3,1);imshow(greyData);title('灰度图');
subplot(1,3,2);imshow(distData);title('加入高斯白噪声后的图像');
subplot(1,3,3);imshow(filtData);title('低通滤波后的图像');

%第三步：锐化
add1 =imfilter(greyData,fspecial('sobel'));%通过filter加sobel算子锐化
out1=add1+greyData;%边缘特征与原图相加

add2 =imfilter(greyData,fspecial('laplacian'));%通过filter加laplacian算子锐化
out2=add2+greyData;%边缘特征与原图相加

%画出锐化前后的对比图像
figure(3);
subplot(1,3,3);
subplot(1,3,1);imshow(greyData);title('原始灰度图');
subplot(1,3,2);imshow(out1);title('sobel算子锐化结果');
subplot(1,3,3);imshow(out2);title('laplacian算子锐化结果');



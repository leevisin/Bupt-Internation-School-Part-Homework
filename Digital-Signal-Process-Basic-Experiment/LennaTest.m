%���㲽����ͼƬ��
data = imread('Lenna.png');%����ͼƬ
dataEn = entropy(data);%����
dataAve = mean2(data);%���ֵ
dataVar = var(double(data(:)));%�󷽲�

%��һ����ת��Ϊ�Ҷ�ͼ��
greyData = rgb2gray(data);%ͼ��ҶȻ�

greyEn = entropy(greyData);%����
greyAve = mean2(greyData);%���ֵ
greyVar = var(double(greyData(:)));%�󷽲�
greyDataF = fftshift(fft2(double(greyData)));%����Ҷ�任
greyMagnitude = log(1+abs(greyDataF));%�����

figure(1);%�����Ҷ�ͼ�����ͼ��
subplot(2,2,4);
subplot(2,2,1);imshow(data);title('ԭͼ');
subplot(2,2,2);imshow(greyData);title('�Ҷ�ͼ');
subplot(2,2,3);imshow(greyMagnitude,[]);title('�Ҷ�ͼ�ķ�����');
subplot(2,2,4);imhist(data);title('�Ҷ�ֱ��ͼ');


%�ڶ������Ӹ�˹���������õ�ͨ�˲���ȥ������
distData = imnoise(greyData, 'gaussian', 0, 10^2/255^2);%����ֵΪ0������Ϊ10^2/255^2�ĸ�˹�����ӵ�ͼ��greyData��
distEn = entropy(distData);%����
distAve = mean2(distData);%���ֵ
distVar = var(double(distData(:)));%�󷽲�

%��ͨ�˲�
distDataF = fftshift(fft2(double(distData)));%����Ҷ�任
[m, n] = size(distDataF);%���ͼ���С
mMid = fix(m/2);%��������
nMid = fix(n/2);%��������
lpfData = zeros(m,n);%��ʼ��
d0=50;%��ֵ ��һ��Ϊ50 �ڶ���Ϊ5
for i=1:m %����ͼ������
    for j=1:n
        d=sqrt((i -mMid)^2+(j-nMid)^2);%�����ͨ�˲��������
        if d<=d0
            h(i,j)=1; 
        else
            h(i,j)=0; %����ͨ�˲�������
        end
        lpfData(i,j)=h(i,j)*distDataF(i,j);  
    end
end %���е�ͨ�˲�  
filtData=ifftshift(lpfData);%������Ҷ�任
filtData=uint8(real(ifft2(filtData)));%ȡʵ������

filtEn = entropy(filtData);%����
filtAve = mean2(filtData);%���ֵ
filtVar = var(double(filtData(:)));%�󷽲�

%�����˲�ǰ��ĶԱ�ͼ��
figure(2);
subplot(1,3,1);imshow(greyData);title('�Ҷ�ͼ');
subplot(1,3,2);imshow(distData);title('�����˹���������ͼ��');
subplot(1,3,3);imshow(filtData);title('��ͨ�˲����ͼ��');

%����������
add1 =imfilter(greyData,fspecial('sobel'));%ͨ��filter��sobel������
out1=add1+greyData;%��Ե������ԭͼ���

add2 =imfilter(greyData,fspecial('laplacian'));%ͨ��filter��laplacian������
out2=add2+greyData;%��Ե������ԭͼ���

%������ǰ��ĶԱ�ͼ��
figure(3);
subplot(1,3,3);
subplot(1,3,1);imshow(greyData);title('ԭʼ�Ҷ�ͼ');
subplot(1,3,2);imshow(out1);title('sobel�����񻯽��');
subplot(1,3,3);imshow(out2);title('laplacian�����񻯽��');



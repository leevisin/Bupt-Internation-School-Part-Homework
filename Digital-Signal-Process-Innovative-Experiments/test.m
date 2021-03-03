% read audio from file
filename = 'input.wav';
[y,fs] = audioread(filename); % read audio from file, return sample data y and sampling rate fs 
%sound(y,fs);

% show the time domain waveform of the original signal
N = length(y); % number of samples 
time = 1/fs * (0:N-1); % 1/fs is sample interval
figure(1)
subplot(2,1,1);plot(time,y);title('初始无混响信号波形');

% show the frequency domain waveform of the original signal
y_FFT = fft(y,N); % returns the n-point DFT
f_p = fs / N * (0:N/2-1);
subplot(2,1,2);plot(f_p,real(y_FFT(1:N/2)));title('初始无混响信号频谱');

% read audio from file
filename = 'output1.wav';
[y,fs] = audioread(filename); % read audio from file, return sample data y and sampling rate fs 
% sound(y,fs);

% show the time domain waveform of the original signal
N = length(y); % number of samples 
time = 1/fs * (0:N-1); % 1/fs is sample interval
figure(2)
subplot(2,1,1);plot(time,y);title('只有混响信号波形');

% show the frequency domain waveform of the original signal
y_FFT = fft(y,N); % returns the n-point DFT
f_p = fs / N * (0:N/2-1);
subplot(2,1,2);plot(f_p,real(y_FFT(1:N/2)));title('只有混响信号频谱');

% add noise
noise = 0.01 * randn(N,1); % generate random noise of equal length
y_mix = y + noise; % mix original audio and noise
% sound(y_mix,fs);

% show the time domain waveform of the mixed signal
figure(3)
subplot(2,1,1);plot(time,y_mix);title('加噪后信号波形');

% show the frequency domain waveform of the mixed signal
y_mix_FFT = fft(y_mix,N);
subplot(2,1,2);plot(f_p,real(y_mix_FFT(1:N/2)));title('加噪后信号频谱');

% digital low-pass technical index
Ft = fs / N; % sampling rate fs, number of samples
Fp = 3000 * 2 * Ft;
Fs = 3600 * Ft; 
Wp = 2 * pi * Fp;
Ws = 2 * pi * Fs;
wp = Wp / fs;
ws = Ws / fs;
alpha_p = 1;  %  passband ripple
alpha_s = 10; %  stopband ripple

% analog low-pass technical index
T = 1;
omega_p = 2/T * tan(wp / 2); % omega_p: passband edge frequency 
omega_s = 2/T * tan(ws / 2); % omega_s: stopband edge frequency

% design analog filter
[n_order,wc] = buttord(omega_p,omega_s,alpha_p,alpha_s,'s'); % calculate filter end N and 3dB final frequency wc
[B,A] = butter(n_order,wc,'s');

% convert to digital filter
[Bz,Az] = bilinear(B,A,1/T);

% filter out the noise
y_filt = filter(Bz,Az,y_mix);
sound(y_filt,fs);

% show the time domain waveform after filter
figure(4)
subplot(2,1,1);plot(time,y_filt);title('加噪滤波后信号波形');

% show the frequency domain waveform after filter
y_filt_FFT = fft(y_filt,N);
subplot(2,1,2);plot(f_p,real(y_filt_FFT(1:N/2)));title('加噪滤波后信号频谱');
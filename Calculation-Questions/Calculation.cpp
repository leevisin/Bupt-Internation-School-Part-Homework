#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdlib.h>
#include <errno.h>
int valid(char *string);
void readfile(FILE *record);
void way(int c);
FILE *test;
FILE *record;
int scores=0;
int a,b,t,i;
char c; 
int result[10];
char ID[6];
int main()
{
	//Check if the input is valid
	int k=0;
	printf("Please Enter Your ID Number:");
	do{
		if(k>0)
			printf("Your ID Number Includes 2 Letters and 4 Digits:");
		k++;
		 scanf("%s",ID);
	}while(strlen(ID)!=6||valid(ID)!=1);	//If the length is not 6, it will ask to put again, and the speed will be fast

	int num,i;
	int add=0,sub=0,mul=0,div=0;
	clock_t start, finish;
	double duration;
	while(1)								//Use loop to ask the user to do what
	{
		printf("What do you want to do?\n");
		printf("1.Start a test\n");
		printf("2.Check scores\n");
		printf("3.Exit\n");
		scanf("%d",&num);
		if(num==1)							//Test
		{
	 		//File open
			if( (test=fopen("test.txt","w")) == NULL ){
       		perror("test.txt");
       	 	exit(1);
		 	}
			//start the timer 
			start = clock();
			for(i=0;i<8;i++)				//The first eight questions are added, subtracted, multiplied, and divided by two
			{
				srand((unsigned)clock());
				c=(rand()%4)+1;		
				switch(c){
					case 1: if(add<2){
									way(c);
									add++;
									break;}
							else{
									i--;
									break;}
					case 2: if(sub<2){
									way(c);
									sub++;
									break;}
							else{
									i--;
									break;}
					case 3: if(mul<2){
									way(c);
									mul++;
									break;}
							else{
									i--;
									break;}
					case 4: if(div<2){
									way(c);
									div++;
									break;}
							else{
									i--;
									break;}			
				}
			}	
			for(i=8;i<10;i++)				//The last two questions are added, subtracted, multiplied, divided, and randomly repeated.
			{
				srand((unsigned)clock());
				c=(rand()%4)+1;		
				switch(c){
					case 1: if(add<3){
									way(c);
									add++;
									break;}
							else{
									i--;
									break;}
					case 2: if(sub<3){
									way(c);
									sub++;
									break;}
							else{
									i--;
									break;}
					case 3: if(mul<3){
									way(c);
									mul++;
									break;}
							else{
									i--;
									break;}
					case 4: if(div<3){
									way(c);
									div++;
									break;}
							else{
									i--;
									break;}			
				}
			}
		
			add=sub=mul=div=0;				//If the user what to test twice or more, it will work well
		//finish timer 
		finish= clock();
		duration = (double)(finish- start) / CLOCKS_PER_SEC;			//Time used
		printf("The time is %.2lfs.\n",duration);
		printf("Your scores are %d.\n",scores);
		//Write into the file(record.txt)
		if( (record=fopen("record.txt","a")) == NULL){
		        printf("Failure to open record.txt! \n");
		        exit(1);
  		}
		fprintf(record,"%s %d %.2lf seconds\n",ID,scores,duration); 
 		fclose(record);
	
		fflush(test);
	    rewind(test);
	    fclose(test);			//Close the file
	   	//print the problem, correct answer and the user' answer by reading file
	    if( (test=fopen("test.txt","r")) == NULL ){
	        perror("test.txt");
	        exit(1);
	 	}
		int pro1[10],pro3[10],pro4[10],pro5[10];
		char pro2[10];
		//Show question | answer | his answer
		printf("Prob. | Correct Answ. | Ur Answ\n");
		for(i=0;i<10;i++)
		{
			fscanf(test,"%d%c%d\t%d\t%d\n",&pro1[i],&pro2[i],&pro3[i],&pro4[i],&pro5[i]);
			printf("%d%c%d\t    %d\t\t    %d\n",pro1[i],pro2[i],pro3[i],pro4[i],pro5[i]);
		}
		i=0;
		scores=0;			//If this student wants to do test again, we need to empty the scores last time 
	    fclose(test);
		}
	 	//Searches the file 'record.txt' and lists all the historical scores for this student
		if(num==2)
		{
			printf("Your previous records are:\n");
	        readfile(record);
		}
		if(num==3)  //Jump out of the loop, and exit
			break;
	}
	
	return 0;
}

int valid(char *string){
	int i,letter=0,number=0;
	for(i=0;i<2;i++)
		{
			if((string[i]>='a' && string[i]<='z') || (string[i]>='A' && string[i]<='Z'))	//How many letters
				letter+=1;
		}
 	for(i=2;i<6;i++)
	 	{
			if(string[i]>='0' && string[i]<='9')		//How many numbers
				number+=1;
		}
	if(letter == 2 && number == 4)						//If the string is valid, return 1
		return 1;
	return 0;											//If it is invalid, return 0
} 



void way(int c){
				a=(rand()%100);
				b=(rand()%100);							//both a,b are random
				switch(c){
				case 1: while(a+b>=100)					
							b=(rand()%100);
						printf("%d + %d = ",a,b);
						scanf("%d",&result[i]);
						fprintf(test,"%d+%d\t%d\t%d\n",a,b,a+b,result[i]);
						if((a+b)==result[i])
							scores += 10;
						break;
				case 2: if(a<b)							//If the subtracted number is less than the minus number, swap two numbers
						{
							t=a;a=b;b=t;
						}
						printf("%d - %d = ",a,b);
						scanf("%d",&result[i]);
						fprintf(test,"%d-%d\t%d\t%d\n",a,b,a-b,result[i]);
						if(result[i]==a-b)
							scores+=10;
						break;
				case 3:	a=(rand()%9+1); 
						b=(rand()%9+1); 				//Let a range between 2 and 15
					//	while(a*b>=100)						
					//		b=(rand()%100);				//b is affected by a
						printf("%d ¡Á %d = ",a,b);
						scanf("%d",&result[i]);
						fprintf(test,"%d*%d\t%d\t%d\n",a,b,a*b,result[i]);
						if(a*b==result[i])
							scores += 10;
						break;
				case 4: a=(rand()%9+1); 
						b=(rand()%9+1); 					//Let a range between 2 and 15 
						//while(a*b>=100)
						//	b=(rand()%100);				//b is affected by a
						printf("%d ¡Â %d = ",a*b,a);
						scanf("%d",&result[i]);
						fprintf(test,"%d/%d\t%d\t%d\n",a*b,a,b,result[i]);
						if(b==result[i])
							scores += 10;
						break;	
				}
} 

void readfile(FILE *record)
{
	char *str1,*ptr;
	char *res[999];
	int p = 0;
	FILE *pFile;
	char mystring[1000];
	pFile = fopen("record.txt","r");
    if (pFile == NULL)
        perror ("Error opening file");
    else {
        while (fgets (mystring , 100 , pFile) != NULL )
            {				
				int len = strlen(mystring);				//Read the record.tet by line
				if(mystring[len-1]=='\n')				//Think of a line as a string
  					mystring[len-1] = '\0';

				char* tmp = (char*)malloc(100*sizeof(char));
            	memcpy(tmp,mystring,len);				//usage memcpy(dest, src, strlen(src)); 
            	res[p++] = tmp;
				for(i=0;i<p;i++)
				{
					ptr = strstr(res[i],ID);
				}										//Determine whether the string containing the ID
				if( ptr != NULL )						//If it does contain the string of ID 
					printf("%s\n",ptr);					//Printf the string
			}
        fclose (pFile);									//Close the file 
    }	
}




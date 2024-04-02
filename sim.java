import java.util.*;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException; 
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math.*;
import java.text.DecimalFormat;
public class sim{
    //gshareHistory table update based on actual prediction
    public static String gshareTableUpdate(String  gshareHistoryTable, String prediction){
        String gshareHistoryTable1="";
        for(int k=0;k<gshareHistoryTable.length();k++){
            char s;
            if(prediction.equals("t")&& k==0 )
                s='1';
            else if(prediction.equals("n")&& k==0 )
                s='0';
            else 
                s=gshareHistoryTable.charAt(k-1);
            gshareHistoryTable1+=s;
        }
        return gshareHistoryTable1;

    }
    //smith logic
    public static void smith(String[] branchAddress,String[] instTaken, double b,String trace_file){
        DecimalFormat df=new DecimalFormat("0.00");
        double totalStates=Math.pow(2,b);
        int counter=0,missPrediction=0,middleState=0,maxState=0;
        maxState=(int)totalStates-1;
        String prediction;
        middleState=((int)totalStates/2)-1;
        if(b==1){
            counter=1;
            for(int i=0;i<instTaken.length;i++){
                if(counter==1)
                    prediction="t";
                else 
                    prediction="n";
                    
                if(instTaken[i].equalsIgnoreCase(prediction)){
                    counter=counter;
                    
                }else{
                    missPrediction++;
                    if(prediction=="t")
                        counter=0;
                    else
                        counter=1;
                }
                }
        }else{
        counter=(int)totalStates/2;
        for(int i=0;i<instTaken.length;i++){
            if(counter>middleState)
                prediction="t";
            else 
                prediction="n";
                
            if(instTaken[i].equalsIgnoreCase(prediction)){
                if(counter>middleState && counter!=maxState){
                    counter++;
                }else if(counter!=0 && counter<=middleState){
                    counter--;
                }
            }else{
                    missPrediction++;
                    if(prediction=="t")
                        counter--;
                    else
                        counter++;
            }
        }
        }
                
        double missPredictionRate = (Double.valueOf(missPrediction)/(instTaken.length))*100;
        System.out.println("COMMAND ./sim smith "+ (int)b +" "+trace_file );
        System.out.println("number of predictions:"+" "+instTaken.length);
        System.out.println("number of mispredictions:"+missPrediction);
        System.out.println("misprediction rate :"+df.format(missPredictionRate)+"%");
        System.out.println("FINAL COUNTER CONTENT:"+counter);
        
        
    }
    //bimodal logic
    public static void bimodal(String[] branchAddress,String[] instTaken,double m1,String trace_file) {
        DecimalFormat df=new DecimalFormat("0.00");
        int totalStates=(int)Math.pow(2,m1);
        int missPrediction=0;
        String prediction;
        int[] predictionTable = new int[totalStates];
        for(int i=0;i<totalStates;i++){
            predictionTable[i]=4;
        }
        for(int i=0;i<instTaken.length;i++){
            int decimalFormat=Integer.parseInt(branchAddress[i],16);
            int bitWise=decimalFormat>>2;
            String binaryFormat=Integer.toBinaryString(bitWise);
            int index=Integer.parseInt(binaryFormat.substring(binaryFormat.length()-(int)m1,binaryFormat.length()),2);
            
            if(predictionTable[index]>3)
               prediction="t";
            else 
               prediction="n";
            if(instTaken[i].equalsIgnoreCase(prediction)){
                if(predictionTable[index]>3 && predictionTable[index]!=7)
                    predictionTable[index]++;
                else if(predictionTable[index]!=0 && predictionTable[index]<4)
                    predictionTable[index]--;

            }else{
                missPrediction++;
                if(prediction=="t")
                    predictionTable[index]--;
                else
                    predictionTable[index]++;
            }
            
        }
        double missPredictionRate = (Double.valueOf(missPrediction)/(instTaken.length))*100;
        System.out.println("COMMAND ./sim smith "+ (int)m1 +" "+trace_file );
        System.out.println("number of predictions:"+" "+instTaken.length);
        System.out.println("number of mispredictions:"+missPrediction);
        System.out.println("misprediction rate:"+df.format(missPredictionRate)+"%");
        System.out.println("FINAL BIMODAL CONTENT:");
        for(int i=0;i<predictionTable.length;i++){
            System.out.println(i+" "+predictionTable[i]);
        }

    }
    //gshare logic
    public static void gshare(String[] branchAddress,String[] instTaken,double m1,double n,String trace_file) {
        DecimalFormat df=new DecimalFormat("0.00");
        int totalStates=(int)Math.pow(2,m1);
        int missPrediction=0;
        String prediction="";
        int[] predictionTable = new int[totalStates];
        String s = String.join("", Collections.nCopies((int)n, String.valueOf("0")));
        for(int i=0;i<totalStates;i++){
            predictionTable[i]=4;
        }
        for(int i=0;i<instTaken.length;i++){
            int decimalFormat=Integer.parseInt(branchAddress[i],16);
            int bitWise=decimalFormat>>2;
            String binaryFormat=Integer.toBinaryString(bitWise);
            int index=Integer.parseInt(binaryFormat.substring(binaryFormat.length()-(int)m1,binaryFormat.length()),2);
            int gshareI=Integer.parseInt(s,2);
            int gshareIndex=index^gshareI;
            if(predictionTable[gshareIndex]>3)
               prediction="t";
            else 
               prediction="n";
            if(instTaken[i].equalsIgnoreCase(prediction)){
                if(predictionTable[gshareIndex]>3 && predictionTable[gshareIndex]!=7)
                    predictionTable[gshareIndex]++;
                else if(predictionTable[gshareIndex]!=0 && predictionTable[gshareIndex]<4)
                    predictionTable[gshareIndex]--;

            }else{
                missPrediction++;
                if(prediction=="t")
                    predictionTable[gshareIndex]--;
                else
                    predictionTable[gshareIndex]++;
            }

            s=gshareTableUpdate(s,instTaken[i]);
            
            
        }
        double missPredictionRate = (Double.valueOf(missPrediction)/(instTaken.length))*100;
        System.out.println("COMMAND ./sim smith "+ (int)m1 +" "+ (int)n+" "+trace_file );
        System.out.println("number of predictions:"+" "+instTaken.length);
        System.out.println("number of mispredictions:"+missPrediction);
        System.out.println("misprediction rate:"+df.format(missPredictionRate)+"%");
        System.out.println("FINAL GSHARE CONTENT:");
        for(int i=0;i<predictionTable.length;i++){
            System.out.println(i+" "+predictionTable[i]);
        }
    }
    //hybrid logic
    public static void hybrid(String[] branchAddress,String[] instTaken,double k,double m1,double n,double m2,String trace_file){
        DecimalFormat df=new DecimalFormat("0.00");
        int totalStates=(int)Math.pow(2,k);
        int missPrediction=0;
        String predictionG="";
        String predictionB="";
        String gshareHistory = String.join("", Collections.nCopies((int)n, String.valueOf("0")));
        int[] predictionTableH = new int[totalStates];
        int[] predictionTableG = new int[(int)Math.pow(2,m1)];
        int[] predictionTableB = new int[(int)Math.pow(2,m2)];
        for(int i=0;i<totalStates;i++){
            predictionTableH[i]=1;
        }
        for(int i=0;i<predictionTableG.length;i++){
            predictionTableG[i]=4;
        }
        for(int i=0;i<predictionTableB.length;i++){
            predictionTableB[i]=4;
        }
        for(int i=0;i<instTaken.length;i++){
            int decimalFormat=Integer.parseInt(branchAddress[i],16);
            int bitWise=decimalFormat>>2;
            String binaryFormat=Integer.toBinaryString(bitWise);
            int indexH=Integer.parseInt(binaryFormat.substring(binaryFormat.length()-(int)k,binaryFormat.length()),2);
           
            if(predictionTableH[indexH]>1){
            int decimalFormatG=Integer.parseInt(branchAddress[i],16);
            int bitWiseG=decimalFormatG>>2;
            String binaryFormatG=Integer.toBinaryString(bitWiseG);
            int indexG=Integer.parseInt(binaryFormatG.substring(binaryFormatG.length()-(int)m1,binaryFormatG.length()),2);
            int gshareI=Integer.parseInt(gshareHistory,2);
            int gshareIndex=indexG^gshareI;
            if(predictionTableG[gshareIndex]>3)
               predictionG="t";
            else 
               predictionG="n";
            if(instTaken[i].equalsIgnoreCase(predictionG)){
                if(predictionTableG[gshareIndex]>3 && predictionTableG[gshareIndex]!=7)
                    predictionTableG[gshareIndex]++;
                else if(predictionTableG[gshareIndex]!=0 && predictionTableG[gshareIndex]<4)
                    predictionTableG[gshareIndex]--;

            }else{
                missPrediction++;
                if(predictionG=="t")
                    predictionTableG[gshareIndex]--;
                else
                    predictionTableG[gshareIndex]++;
            }
            gshareHistory=gshareTableUpdate(gshareHistory, instTaken[i]);
            int decimalFormatB=Integer.parseInt(branchAddress[i],16);
            int bitWiseB=decimalFormatB>>2;
            String binaryFormatB=Integer.toBinaryString(bitWiseB);
            int indexB=Integer.parseInt(binaryFormatB.substring(binaryFormatB.length()-(int)m2,binaryFormatB.length()),2);

            if(predictionTableB[indexB]>3)
                predictionB="t";
            else 
                predictionB="n";
            
            }else if(predictionTableH[indexH]<2){
                int decimalFormatB=Integer.parseInt(branchAddress[i],16);
                int bitWiseB=decimalFormatB>>2;
                String binaryFormatB=Integer.toBinaryString(bitWiseB);
                int indexB=Integer.parseInt(binaryFormatB.substring(binaryFormatB.length()-(int)m2,binaryFormatB.length()),2);

                if(predictionTableB[indexB]>3)
                    predictionB="t";
                else 
                    predictionB="n";
                if(instTaken[i].equalsIgnoreCase(predictionB)){
                    if(predictionTableB[indexB]>3 && predictionTableB[indexB]!=7)
                        predictionTableB[indexB]++;
                    else if(predictionTableB[indexB]!=0 && predictionTableB[indexB]<4)
                        predictionTableB[indexB]--;

                }else{
                    missPrediction++;
                    if(predictionB=="t")
                        predictionTableB[indexB]--;
                    else
                        predictionTableB[indexB]++;
                }
                int decimalFormatG=Integer.parseInt(branchAddress[i],16);
                int bitWiseG=decimalFormatG>>2;
                String binaryFormatG=Integer.toBinaryString(bitWiseG);
                int indexG=Integer.parseInt(binaryFormatG.substring(binaryFormatG.length()-(int)m1,binaryFormatG.length()),2);
                int gshareI=Integer.parseInt(gshareHistory,2);
                int gshareIndex=indexG^gshareI;
                if(predictionTableG[gshareIndex]>3)
                    predictionG="t";
                else 
                    predictionG="n";
                gshareHistory=gshareTableUpdate(gshareHistory, instTaken[i]);
            }
        if(instTaken[i].equalsIgnoreCase(predictionG)&& !instTaken[i].equalsIgnoreCase(predictionB)){
            if(predictionTableH[indexH]<3 && predictionTableH[indexH]>=0)
            predictionTableH[indexH]++;
        }
        if(!instTaken[i].equalsIgnoreCase(predictionG)&& instTaken[i].equalsIgnoreCase(predictionB)){
            if(predictionTableH[indexH]<=3 && predictionTableH[indexH]>0)
            predictionTableH[indexH]--;
        }
        
    }
    double missPredictionRate = (Double.valueOf(missPrediction)/(instTaken.length))*100;
    System.out.println("COMMAND ./sim smith "+ (int)k +" "+(int)m1 +" "+(int)n+" "+(int)m2 +" "+trace_file );
    System.out.println("number of predictions:"+" "+instTaken.length);
    System.out.println("number of mispredictions:"+missPrediction);
    System.out.println("misprediction rate:"+df.format(missPredictionRate)+"%");
    System.out.println("FINAL HYBRID CONTENT:");
    for(int i=0;i<predictionTableH.length;i++)
        System.out.println(i+" "+predictionTableH[i]);
    // System.out.println("FINAL GSHARE CONTENT:");
    // for(int i=0;i<predictionTableG.length;i++)
    //     System.out.println(i+" "+predictionTableG[i]);
    // System.out.println("FINAL BIMODAL CONTENT:");
    // for(int i=0;i<predictionTableB.length;i++)
    //     System.out.println(i+" "+predictionTableB[i]);
    }
    public static void main(String[] args) {
        double k=0,b=0,m1=0,m2=0,n=0;
        String trace_file="";
        
        List<String> pipelineInst = new ArrayList<String>();
        String branchType=String.valueOf(args[0]);
        if(branchType.equalsIgnoreCase("smith")){
            b=Integer.parseInt(String.valueOf(args[1]));
            trace_file=String.valueOf(args[2]);
        }else if(branchType.equalsIgnoreCase("bimodal")){
            m1=Integer.parseInt(String.valueOf(args[1]));
            trace_file=String.valueOf(args[2]);
        }else if(branchType.equalsIgnoreCase("gshare")){
            m1=Integer.parseInt(String.valueOf(args[1]));
            n=Integer.parseInt(String.valueOf(args[2]));
            trace_file=String.valueOf(args[3]);
        }else if(branchType.equalsIgnoreCase("hybrid")){
            k=Integer.parseInt(String.valueOf(args[1]));
            m1=Integer.parseInt(String.valueOf(args[2]));
            n=Integer.parseInt(String.valueOf(args[3]));
            m2=Integer.parseInt(String.valueOf(args[4]));
            trace_file=String.valueOf(args[5]);
        }
        try {
            File myObj = new File(trace_file);
            Scanner s = new Scanner(new BufferedReader(new FileReader(myObj)));
            while (s.hasNextLine()) {
              pipelineInst.add(s.nextLine());
            }
            s.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        int inst=0;
        String[] branchAddress=new String[pipelineInst.size()];
		String[] instTaken=new String[pipelineInst.size()];
        for(String eachPipelineInstruction:pipelineInst){
            branchAddress[inst]=eachPipelineInstruction.split(" ")[0];
            instTaken[inst]=eachPipelineInstruction.split(" ")[1];
                
            inst++;
        } 
        if(branchType.equalsIgnoreCase("smith")){
                smith(branchAddress,instTaken,b,trace_file);
        }else if(branchType.equalsIgnoreCase("bimodal")){
            bimodal(branchAddress,instTaken,m1,trace_file);
        }else if(branchType.equalsIgnoreCase("gshare")){
            gshare(branchAddress,instTaken,m1,n,trace_file);
        }else if(branchType.equalsIgnoreCase("hybrid")){
            hybrid(branchAddress,instTaken,k,m1,n,m2,trace_file);
        }
    }
}
import interfascia.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Crypt extends PApplet {

//Colorectal model of Carcinogenesis. Arturo Araujo and Albert Ruebben
//Released under Apache 2.0 License, Version 2.0 requiring preservation of the copyright notice and disclaimer.
//License  allows the user of the software the freedom to use the software for for academic purposes but commercial use is prohibited.
//Version 2.5 changes color depending on number of divisions
//Version 2.7 includes buttons to change the visualization. State becomes a variable for whatever we want to represent visually
//Make up to 14 columns
//version 3.3 includes movement downward in the SCC
//Version 3.5 Updated to work with Processing3. Canvas size has to be declared now from the start.
//Version 4.3 Logs the time it takes for monoclonality
//Version 4.5 Can switch between timed sumulation ending or until monoclonality
//Version 4.7 Made the death probabilities more explicit
//Version 4.8 Solved the TAC cells not registering
//Version 5.0 made WNT into a gradient. Having problems recording...
//Version 5.1 added lateral replenishing od dead cells
//Version 5.2 extend lateral replenishing during SCC death
//Version 5.3 time parametrized with rows x columns; death probabilities extended to three decimal points; cells get shedded if outside of field of vision
//Version 5.4 adjust timescale to match Ritsma paper + extract telomeres
//Version 5.6 set WNT as a constant, not a gradient and measure division across rows
//Version 6.0b There is a gap on the EGF/WNT division rate.
//Version 6.0c Investigating why there's more division at the bottom
//Version 6.2 Correct cell cycle to make Stem cells divide once a day
//Version 6.3 Change lineage visualization
//Version 6.4 Updated time to reflect 1 round of update
//Version 6.5 Used for Alife paper, cleaned up dead code
//Version 6.5_KB_a Added testing with random seeds, added chance of basic mutation of two genes controlling division rates (one oncogene, one TS) at every division. Added #mutations visualisation. Fixed visualation button choice.
//Version 6.5_KB_b Limited mutation to stems cells at each event of monoclonality. Output mutation history
//Version 6.5_KB_c Pathway interaction of genes on proliferation and death probabilities and cell cycle replaces two gene model. One mutated cell with selected mutations is added as a stem cell (random turned off).
//Version 6.5_KB_e,f Allow choice between GUI and batch run options
//Version 6.5_KB_g Update of pathway for revised 7 gene gene network file, added cell fate vis
//Version 6.5_KB_h Added start/stop simulation and take photo functionality,
//Version 6.5_KB_i Updated pathway, added outputs for diversity tracking





    // testing options
    static boolean testing = false;

    //GUI controls
    static boolean GUI_On = true;
    boolean mutateNext = false; // ready to add another mutation
    static int mutant = 1; // next mutant lineage number
    GUIController c;
    IFButton b1, b2, b3, b4;
    IFLabel l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12, l13, l14, l15, l16, l17, l18, l19, l20, l21, l22, l23, l24;
    IFLabel l25, l26, l27, l28, l29, l30, l31, l32, l33, l34, l35, l36, l37, l38, l39, l40;
    IFTextField t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14;
    IFTextField a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30, a31;
    //boolean pause = true; //simulation paused or running
    boolean resetNow = true; //do full button reset in setup

    //batch run options
    static int run =0; // mutation number for batch
    static int [] runList =  {1,5,19,26}; //mutation run list
    static int iteration = 0; //iteration counter
    static int noIterations = 20; //number of iterations to run
    static int [] testVariable = {25,24,18,12,6,0};
    static int mutantValue = runList[run];
    boolean pause = true;

    //mutations on
    static boolean mutationsOn = true; //allow mutations to occur
    static boolean chooseMutationOn = true; //select a specific mutation rather than random mutations
    static boolean multiLineageStart = false; //1 lineage or multiple at the start
    float delayCount = 0; //time since last mutation added
    boolean day1Mutation = false; //has a mutation been added in day one
    float lastDay = 0; // what was the last day a mutation was added
    float[] mutationHistory = new float[200]; //list of mutations made to the crypt
    int nextPosition = 0; //next position in the mutation list into which to store the next mutation details
    boolean firstMono = false;  // has the first occurance on monoclonality occured
    boolean firstExtinct = false; // has the first time when mutant cells were extinct occured
    float firstMonoTime = 0; // what the time step of the first event of monoclonality
    float firstExtinctTime = 0; // what was the time step of the first event of mutant extinction
    int mutantCount = 0; // how many mutant cells are included in the crypt
    int SCCmutantCount = 0; // how many mutant stem cells are included in the crypt
    static int sheddingHeight = 200; //height above which death always certain
    int WT = 0;      //# cells of wild type
    int mutant1 = 0; //# cells of mutant type x
    int mutant2 = 0; //# cells of mutant type x
    int mutant3 = 0; //# cells of mutant type x
    int mutant4 = 0; //# cells of mutant type x
    int mutant5 = 0; //# cells of mutant type x
    int mutant6 = 0; //# cells of mutant type x
    int mutant7 = 0; //# cells of mutant type x
    int mutant8 = 0; //# cells of mutant type x
    int mutant9 = 0; //# cells of mutant type x
    int mutant10 = 0; //# cells of mutant type x

    ArrayList cells; // This defines the list of cells. Their properties are in the Cell class
    int cellBag;//to pick only one cell per round
    //PFont f=createFont("Arial",15,true); //This defines the fonts used for the display of text
    PImage img1, img2; //Loads an image
    PrintWriter textFile;// this prints out a file with relevant information

    static int endsim=1; //0- for time to monoclonality finish, 1- for timed finish
    static int timeend=100;//time to end simulation in days
    static float updateCycles=1;
    //For the Growth Factor input rectangles
    static int wntLevel=100; //Percentage Level of WNT
    static int egfLevel=100; //Percentage Level of EGF

//static int deathSCC=50000;// 1 in 100.000 probability of death in SCC - goes from 0 at the bottom to deathSCC at the top of EFG (up to 100)
//static int basalSCCdeath=10000;//Aditional chance of death for the basal SCC layer. Normally it's zero bacause the EGF is max
//static int deathSCC=0;// 1 in 100.000 probability of death in SCC (100000 max)- goes from 0 at the bottom to deathSCC at the top of EFG (up to 100)
//static int basalSCCdeath=0;//Aditional chance of death for the basal SCC layer. Normally it's zero bacause the EGF is max
//static int deathTAC=100000;// from 0.000% moves up to 100.000% chance of dying in a gradient, **This is the other way around! Recheck! Fixed
//static int deathEP=100000;// 100.000 in 100.000 probability of death in differentiatied layer

//static int divideSCC=100;//  X% (up to 100%) probability of division in SCC
//static int divideTAC=100;// from X% (up to 100%) chance of division lowers to 0% as it moves up in a gradient,
//static int divideEP=0;// 0 in 100 probability of division in differentiatied layer

    static int cryptWidth=16; //width of the Crypt (20 max)
    static int cryptLength=24; //length of the Crypt, 25 for murine, 80 for human

    static int sccNo=5;// number of Stem Cells in the Crypt (20 max)
    static int telomereLength=5; //initial size of telomeres, decreases by 1 every division, cell arrrests after depletion
//static int timescale=int(cryptWidth*cryptLength*3/2);//to reparametrize the timescale. The bigger the crypt, the longer the cell cycle takes?


    static int[] stemXtracker= new int[sccNo];
    static int[] stemMtracker= new int[sccNo];

    static float[] divisionYtracker= new float[cryptLength+1];//to measure division along the Y axis
    static int canvas_y=700+80;//bottom+80

    static int kColumns=ceil((float)cryptWidth/sccNo);//
    static int carrying_cap= sccNo*kColumns*cryptLength; //this determines when death kicks in. It won't until the carrying capacity is surpased

    static int rectX0, rectX1, rectX2, rectX3, rectX4, rectX5, rectY;      // Position of square button
    static int rectSize = 30;     // Diameter of rect

    static int egfSize=(int)(-3*egfLevel);//
    static int wntSize=(int)(-3*wntLevel/10-14);// additional 14 to ensure bottom three layers are stem cells in base case

    int rectColor, baseColor;
    int rectHighlight;
    int currentColor;
    boolean rect0Over = false;
    boolean rect1Over = false;
    boolean rect2Over = false;
    boolean rect3Over = false;
    boolean rect4Over = false;
    boolean rect5Over = false;

    static int vis=0;
    static int timestep=1;// initital time counter (aimed to be parametrized in hours)
    static int divNoSCC=0; // used for the average of division in SCC
    static int divNoTAC=0; // used for the average of division in TAC
    static int teloCountTAC=0; //to keep track of average telomere
    static int teloCountSCC=0; //to keep track of average telomere
    static int countTAC=0; //to keep track of average telomere
    static int countSCC=0; //to keep track of average telomere

    static int r=15; //cell radius
    static int bottom=700; //this defines the botom of the crypt.  (y max-10)
    static int xstart=4;//space between x=0 and the start of the crypt
    static int xend=xstart+(sccNo*kColumns)-1;//space between x=0 and the end of the crypt

    static int sccStart;
    static int sccEnd;
    static int SCClimit=bottom-r;// where Stem Cells start

    Logger loggerObject;  // don't use new here please

    static int wntX= 500;
    static int wntY= SCClimit;
    static int egfX= 500 + 2*rectSize;
    static int egfY= SCClimit;

    static int wntTotal=wntY+wntSize;//same as Cell
    static int egfTotal=egfY+egfSize;//probably best to collapse these

    //movement of basal SCcompartment
    int SCCMoveL = 12;//testVariable[run];//12% chance of lateral left displacement (remainder to right displacement)
    int SCCMoveU = 76; //100 - SCCMoveL*2;//76% chance of up & random side displacement
    int SCCMoveU_C = SCCMoveU; //cumulative
    int SCCMoveL_C = SCCMoveU_C + SCCMoveL;//cumulative

    //Movement of suprabasal SC Compartment
    int SBMoveLU = 12; //12% chance of lateral left & up displacement
    int SBMoveRU = 12;//12% chance of lateral right & up displacement
    int SBMoveL = 12;//12% chance of lateral left displacement
    int SBMoveR = 12; //12% chance of lateral right displacement
    int SBMoveU = 53; //46% chance of up displacement (remaining % goes to down)
    int SBMoveLU_C = SBMoveLU; //cumulative
    int SBMoveRU_C = SBMoveLU_C + SBMoveRU;//cumulative
    int SBMoveL_C = SBMoveRU_C + SBMoveL;//cumulative
    int SBMoveR_C = SBMoveL_C + SBMoveR;//cumulative
    int SBMoveU_C = SBMoveR_C + SBMoveU;//cumulative

    //Movement of TAC Compartment . Order is Important here.
    int TACMoveLU = 12;//12% chance of lateral left & up displacement
    int TACMoveRU = 12;//12% chance of lateral right & up displacement
    int TACMoveL = 12;//12% chance of lateral left displacement
    int TACMoveR = 12;//12% chance of lateral right displacement
    int TACMoveLD = 12;//12% chance of lateral left & down displacement
    int TACMoveRD = 12;//12% chance of lateral right & down displacement
    int TACMoveU = 20;  //20% chance of up displacement (remaining to down)
    int TACMoveLU_C = TACMoveLU; //cumulative
    int TACMoveRU_C = TACMoveLU_C + TACMoveRU;//cumulative
    int TACMoveL_C = TACMoveRU_C + TACMoveL;//cumulative
    int TACMoveR_C = TACMoveL_C + TACMoveR;//cumulative
    int TACMoveLD_C = TACMoveR_C + TACMoveLD;//cumulative
    int TACMoveRD_C = TACMoveLD_C + TACMoveRD ;//cumulative
    int TACMoveU_C = TACMoveRD_C + TACMoveU;//cumulative

    //cell cycle times
    static int maxCellCycle = 24; //cell cycle time for stem cells
    static int minCellCycle = 12; //cell cycle time for TAC

    //copy number change impact on gene activation levels
    static int copyIncreaseImpact = 110; //output signal 110% of typical if 3 alleles
    static int copyDecreaseImpact = 75; //output signal 75% of typical if 1 allele
    static int copyIncreaseImpactAPC = 110; //output signal 110% of typical if 3 alleles for APC
    static int copyDecreaseImpactAPC = 90; //output signal 90% of typical if 1 allele for APC

    //cell fate thresholds
    static int thresholdCTNNB1 = 25; //threshold for cell fate decisions
    static int thresholdCMYC = 0; //threshold for cell fate decisions


    public void setup() {
        //The y axis is inverted. The top left corner is (0,0), the bottom right corner is (400,610)
        //To draw the rectangles on top for visualization
        rectColor = color(0);
        rectHighlight = color(150);
        rectX0 = width/6- 2*rectSize;
        rectX1 = width/6;
        rectX2 = width/6+2*rectSize;
        rectX3 = width/6+4*rectSize;
        rectX4 = width/6+6*rectSize;
        rectX5 = width/6+8*rectSize;
        rectY = 10;

        // testing
        Random rnd = new Random();
        if (testing == true) {
            randomSeed(1234);
            rnd = new Random(1234);
        }
        else {
            rnd = new Random();
        }

        // GUI control EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
        if(GUI_On == true){
            c = new GUIController (this);
            int yLine1 = 650;
            int yLine2 = 700;
            int xCol1 = 670;
            int spacing = 20;

            background(250);
            noFill();
            stroke(0);
            rect(650, 28, 500, 740);
            rect(660, 595, 480, 155);
            rect(660, 100, 480, 465);
            fill(0);
            textSize(18);
            text("SIMULATION CONTROLS",655, 25);
            textSize(14);
            text("MUTATIONS",665, 590);
            text("SETUP",665, 95);
            textSize(12);
            if(pause == true){text("Paused",910, 68);}
            else {text("Running",910, 68);}

            if(resetNow == true){
                b1 = new IFButton ("Mutate", xCol1, yLine2 + spacing*2, 60);
                b2 = new IFButton ("Reset", xCol1 + 4*spacing, 28 + spacing, 60);
                b3 = new IFButton ("Start/Stop", xCol1 , 28 + spacing, 60);
                b4 = new IFButton ("Capture", xCol1 + 8*spacing, 28 + spacing, 60);
                t1 = new IFTextField("EGFR", xCol1, yLine1, spacing, "0");
                t2 = new IFTextField("KRAS", xCol1 + 2*spacing, yLine1, spacing, "0");
                t3 = new IFTextField("BRAF", xCol1 + 4*spacing, yLine1, spacing, "0");
                t4 = new IFTextField("CMYK", xCol1 + 6*spacing, yLine1, spacing, "0");
                t5 = new IFTextField("AKT1", xCol1 + 8*spacing, yLine1, spacing, "0");
                t6 = new IFTextField("APC", xCol1 + 10*spacing, yLine1, spacing, "0");
                t7 = new IFTextField("CTNNB1", xCol1 + 12*spacing, yLine1, spacing, "0");
                t8 = new IFTextField("EGFR", xCol1, yLine2, spacing, "2");
                t9 = new IFTextField("KRAS", xCol1 + 2*spacing, yLine2, spacing, "2");
                t10 = new IFTextField("BRAF", xCol1 + 4*spacing, yLine2, spacing, "2");
                t11 = new IFTextField("CMYK", xCol1 + 6*spacing, yLine2, spacing, "2");
                t12 = new IFTextField("AKT1", xCol1 + 8*spacing, yLine2, spacing, "2");
                t13 = new IFTextField("APC", xCol1 + 10*spacing, yLine2, spacing, "2");
                t14 = new IFTextField("CTNNB1", xCol1 + 12*spacing, yLine2, spacing, "2");

                l1 = new IFLabel("Enter point mutations:", xCol1, yLine1 - 2*spacing);
                l2 = new IFLabel("Enter copy numbers:", xCol1, yLine2 - spacing);
                l3 = new IFLabel("EGFR", xCol1, yLine1 - spacing);
                l4 = new IFLabel("KRAS", xCol1 + 2*spacing, yLine1 - spacing);
                l5 = new IFLabel("BRAF", xCol1 + 4*spacing, yLine1 - spacing);
                l6 = new IFLabel("CMYK", xCol1 + 6*spacing, yLine1 - spacing);
                l7 = new IFLabel("AKT1", xCol1 + 8*spacing, yLine1 - spacing);
                l8 = new IFLabel("APC", xCol1 + 10*spacing , yLine1 - spacing);
                l9 = new IFLabel("CTNNB1", xCol1 + 12*spacing, yLine1 - spacing);

                int yLine3 = 110;
                int yLine4 = 110;
                int xCol2 = 855;
                int xCol3 = 1090;
                int labelSpaceLeft = 185;
                int labelSpaceRight = 185;
                spacing = 14;

                a1 = new IFTextField("Shedding height (y)", xCol2, yLine3, spacing*3, "200"); //height above which death always certain
                a2 = new IFTextField("Sim end type (0/1)", xCol2, yLine3 + 2*spacing, spacing*3, "1"); //0- for time to monoclonality finish, 1- for timed finish
                a3 = new IFTextField("Wnt level %", xCol2, yLine3 + 4*spacing, spacing*3, "100"); //Percentage Level of WNT
                a4 = new IFTextField("EGF level %", xCol2, yLine3 + 6*spacing, spacing*3, "100"); //Percentage Level of EGF
                a5 = new IFTextField("crypt width (#cells)", xCol2, yLine3 + 8*spacing, spacing*3, "16"); //cells wide
                a6 = new IFTextField("crypt length (#cells)", xCol2, yLine3 + 10*spacing, spacing*3, "24"); //cells high
                a7 = new IFTextField("Stem cell number", xCol2, yLine3 + 12*spacing, spacing*3, "8");
                a8 = new IFTextField("starting telomeres", xCol2, yLine3 + 14*spacing, spacing*3, "5"); //initial size of telomeres, decreases by 1 every division, cell arrrests after depletio
                a9 = new IFTextField("Activation ratio for 1 copy increase", xCol2, yLine3 + 16*spacing, spacing*3, "110");
                a10 = new IFTextField("Activation ratio for 1 copy decrease", xCol2, yLine3 + 18*spacing, spacing*3, "75");
                a11 = new IFTextField("Activation ratio for 1 copy increase - APC", xCol2, yLine3 + 20*spacing, spacing*3, "110");
                a12 = new IFTextField("Activation ratio for 1 copy decrease - APC", xCol2, yLine3 + 22*spacing, spacing*3, "90");
                a13 = new IFTextField("Min cell cycle (multiple of 6)", xCol2, yLine3 + 24*spacing, spacing*3, "12");
                a14 = new IFTextField("Max cell cycle (multiple of 6)", xCol2, yLine3 + 26*spacing, spacing*3, "24");
                a15 = new IFTextField("CTNNB1 activation level", xCol2, yLine3 + 28*spacing, spacing*3, "25");
                a16 = new IFTextField("cMyc activation level", xCol2, yLine3 + 30*spacing, spacing*3, "0");
                a17 = new IFTextField("Simulation end time", xCol3, yLine4 + 0*spacing, spacing*3, "50");
                a18 = new IFTextField("TAC comp. div. up left (%)", xCol3, yLine4 + 2*spacing, spacing*3, "12");
                a19 = new IFTextField("TAC comp. div. up right (%)", xCol3, yLine4 + 4*spacing, spacing*3, "12");
                a20 = new IFTextField("TAC comp. div. left (%)", xCol3, yLine4 + 6*spacing, spacing*3, "12");
                a21 = new IFTextField("TAC comp. div. right (%)", xCol3, yLine4 + 8*spacing, spacing*3, "12");
                a22 = new IFTextField("TAC comp. div. down left (%)", xCol3, yLine4 + 10*spacing, spacing*3, "12");
                a23 = new IFTextField("TAC comp. div. down right (%)", xCol3, yLine4 + 12*spacing, spacing*3, "12");
                a24 = new IFTextField("TAC comp. div. up (%)", xCol3, yLine4 + 14*spacing, spacing*3, "20");
                a25 = new IFTextField("SupB div. up left (%)", xCol3, yLine4 + 17*spacing, spacing*3, "12");
                a26 = new IFTextField("SupB div. up right (%)", xCol3, yLine4 + 19*spacing, spacing*3, "12");
                a27 = new IFTextField("SupB div. left (%)", xCol3, yLine4 + 21*spacing, spacing*3, "12");
                a28 = new IFTextField("SupB div. right (%)", xCol3, yLine4 + 23*spacing, spacing*3, "12");
                a29 = new IFTextField("SupB div. up (%)", xCol3, yLine4 + 25*spacing, spacing*3, "53");
                a30 = new IFTextField("Basal div. up (%)", xCol3, yLine4 + 28*spacing, spacing*3, "76");
                a31 = new IFTextField("Basal div. left (%)", xCol3, yLine4 + 30*spacing, spacing*3, "12");

                l10 = new IFLabel("Shedding height (y):", xCol2 - labelSpaceLeft, yLine3);
                l11 = new IFLabel("Sim end type (0/1):", xCol2 - labelSpaceLeft, yLine3 + 2*spacing);
                l12 = new IFLabel("Wnt level (%):", xCol2 - labelSpaceLeft, yLine3 + 4*spacing);
                l13 = new IFLabel("EGF level (%):", xCol2 - labelSpaceLeft, yLine3 + 6*spacing);
                l14 = new IFLabel("Crypt width (#cells):", xCol2 - labelSpaceLeft, yLine3 + 8*spacing);
                l15 = new IFLabel("Crypt length (#cells):", xCol2 - labelSpaceLeft, yLine3 + 10*spacing);
                l16 = new IFLabel("Stem cells (#cells):", xCol2 - labelSpaceLeft, yLine3 + 12*spacing);
                l17 = new IFLabel("Telomeres (#):", xCol2 - labelSpaceLeft, yLine3 + 14*spacing);
                l18 = new IFLabel("Act. ratio for copy inc. (%):", xCol2 - labelSpaceLeft, yLine3 + 16*spacing);
                l19 = new IFLabel("Act. ratio for copy dec. (%):", xCol2 - labelSpaceLeft, yLine3 + 18*spacing);
                l20 = new IFLabel("Act. ratio for copy inc. APC (%):", xCol2 - labelSpaceLeft, yLine3 + 20*spacing);
                l21 = new IFLabel("Act. ratio for copy dec. APC (%):", xCol2 - labelSpaceLeft, yLine3 + 22*spacing);
                l22 = new IFLabel("Min cell cycle (multiple of 6hrs):", xCol2 - labelSpaceLeft, yLine3 + 24*spacing);
                l23 = new IFLabel("Max cell cycle (multiple of 6hrs):", xCol2 - labelSpaceLeft, yLine3 + 26*spacing);
                l24 = new IFLabel("CTNNB1 threshold lvl (integer):", xCol2 - labelSpaceLeft, yLine3 + 28*spacing);
                l25 = new IFLabel("cMyc threshold lvl (integer):", xCol2 - labelSpaceLeft, yLine3 + 30*spacing);
                l26 = new IFLabel("Sim end time (#days):", xCol3 - labelSpaceRight, yLine4 + 0*spacing);
                l27 = new IFLabel("TAC comp. div. up left (%):", xCol3 - labelSpaceRight, yLine4 + 2*spacing);
                l28 = new IFLabel("TAC comp. div. up right (%):", xCol3 - labelSpaceRight, yLine4 + 4*spacing);
                l29 = new IFLabel("TAC comp. div. left (%):", xCol3 - labelSpaceRight, yLine4 + 6*spacing);
                l30 = new IFLabel("TAC comp. div. right (%):", xCol3 - labelSpaceRight, yLine4 + 8*spacing);
                l31 = new IFLabel("TAC comp. div. down left (%):", xCol3 - labelSpaceRight, yLine4 + 10*spacing);
                l32 = new IFLabel("TAC comp. div. down right (%):", xCol3 - labelSpaceRight, yLine4 + 12*spacing);
                l33 = new IFLabel("TAC comp. div. up (%): \n (TAC remainder goes down)", xCol3 - labelSpaceRight, yLine4 + 14*spacing);
                l34 = new IFLabel("SupB div. up left (%):", xCol3 - labelSpaceRight, yLine4 + 17*spacing);
                l35 = new IFLabel("SupB div. up right (%):", xCol3 - labelSpaceRight, yLine4 + 19*spacing);
                l36 = new IFLabel("SupB div. left (%):", xCol3 - labelSpaceRight, yLine4 + 21*spacing);
                l37 = new IFLabel("SupB div. right (%):", xCol3 - labelSpaceRight, yLine4 + 23*spacing);
                l38 = new IFLabel("SupB div. up (%): \n (SubB remainder goes down)", xCol3 - labelSpaceRight, yLine4 + 25*spacing);
                l39 = new IFLabel("Basal div. up (%):", xCol3 - labelSpaceRight, yLine4 + 28*spacing);
                l40 = new IFLabel("Basal div. left (%): \n (Basal remainder goes right)", xCol3 - labelSpaceRight, yLine4 + 30*spacing);


                b1.addActionListener(this);
                b2.addActionListener(this);
                b3.addActionListener(this);
                b4.addActionListener(this);

                c.add (b1);
                c.add (b2);
                c.add (b3);
                c.add (b4);

                c.add (t1);
                c.add (t2);
                c.add (t3);
                c.add (t4);
                c.add (t5);
                c.add (t6);
                c.add (t7);
                c.add (t8);
                c.add (t9);
                c.add (t10);
                c.add (t11);
                c.add (t12);
                c.add (t13);
                c.add (t14);

                c.add (l1);
                c.add (l2);
                c.add (l3);
                c.add (l4);
                c.add (l5);
                c.add (l6);
                c.add (l7);
                c.add (l8);
                c.add (l9);
                c.add (l10);
                c.add (l11);
                c.add (l12);
                c.add (l13);
                c.add (l14);
                c.add (l15);
                c.add (l16);
                c.add (l17);
                c.add (l18);
                c.add (l19);
                c.add (l20);
                c.add (l21);
                c.add (l22);
                c.add (l23);
                c.add (l24);
                c.add (l25);
                c.add (l26);
                c.add (l27);
                c.add (l28);
                c.add (l29);
                c.add (l30);
                c.add (l31);
                c.add (l32);
                c.add (l33);
                c.add (l34);
                c.add (l35);
                c.add (l36);
                c.add (l37);
                c.add (l38);
                c.add (l39);
                c.add (l40);

                c.add (a1);
                c.add (a2);
                c.add (a3);
                c.add (a4);
                c.add (a5);
                c.add (a6);
                c.add (a7);
                c.add (a8);
                c.add (a9);
                c.add (a10);
                c.add (a11);
                c.add (a12);
                c.add (a13);
                c.add (a14);
                c.add (a15);
                c.add (a16);
                c.add (a17);
                c.add (a18);
                c.add (a19);
                c.add (a20);
                c.add (a21);
                c.add (a22);
                c.add (a23);
                c.add (a24);
                c.add (a25);
                c.add (a26);
                c.add (a27);
                c.add (a28);
                c.add (a29);
                c.add (a30);
                c.add (a31);

                resetNow = false;
            }
        }

        //setup
        loggerObject = new Logger (sketchPath("") + "data/AllSims.txt");  // use new here please

        if (sccNo>14) { //If it goes beyond 14 Stem cells, we colapse it to one column per stem cell
            kColumns=1;
            xend=xstart+sccNo-1;
        }

        cells = new ArrayList(); //This is the actual list of cells.
        img1 = loadImage("days.jpg");//Image that represent the color scale

//We start adding Base Stem cells to the list.
        int contA=1;
        for (int k=xstart;k<=xend;k++){//this is the x axis
            if (k%kColumns==0){
                if (multiLineageStart == true){
                    cells.add(new Cell(r*k,SCClimit,contA, 0, 0, 0, telomereLength,0,0,0,0,0,0,0,2,2,2,2,2,2,2));}
                else {
                    cells.add(new Cell(r*k,SCClimit,1, 0, 0, 0, telomereLength,0,0,0,0,0,0,0,2,2,2,2,2,2,2));}


                stemXtracker[contA-1]= r*k;//keeps track of the true positions of the stem cells
                stemMtracker[contA-1]= contA;//keeps track of the progeny positions of the stem cells

                if (contA==1){
                    sccStart=k;

                }
                else if (contA==sccNo){
                    sccEnd=k;
                }
                contA=contA+1;
            }
        }


//We then add TAC cells to the list.
        for (int l=2;l<cryptLength;l++){//this is the y axis // determines the size of the crypt
            int contB=0;
            for (int k=xstart;k<=xend;k++){//this is the x axis
                if (k%kColumns==0){contB=contB+1;}
                if (multiLineageStart == true){
                    if(contB==0){cells.add(new Cell(r*k,bottom-(l*r), sccNo, 0, 0, 0, telomereLength,0,0,0,0,0,0,0,2,2,2,2,2,2,2));}
                    else {cells.add(new Cell(r*k,bottom-(l*r), contB, 0, 0, 0, telomereLength,0,0,0,0,0,0,0,2,2,2,2,2,2,2));}
                }
                else {
                    cells.add(new Cell(r*k,bottom-(l*r), 1, 0, 0, 0, telomereLength,0,0,0,0,0,0,0,2,2,2,2,2,2,2));}
            }
        };

        //This is to select only one cell each round

        Collections.shuffle(cells, rnd);
        cellBag=cells.size()-1;// Sorted Array of cell positions

        //This controlls how fast and how smooth the simulation runs
        frameRate(40000);
        textFile = createWriter("data/Aneup"+month()+"-"+day()+"-"+hour()+"-"+minute()+"-"+second()+".txt"); //This is the text output with key information
    }

    public void update(int x, int y) {
        if ( overRect(rectX0, rectY, rectSize, rectSize) ) {
            rect0Over = true;
            rect1Over = false;
            rect2Over = false;
            rect3Over = false;
            rect4Over = false;
            rect5Over = false;
        }
        else if ( overRect(rectX1, rectY, rectSize, rectSize) ) {
            rect0Over = false;
            rect1Over = true;
            rect2Over = false;
            rect3Over = false;
            rect4Over = false;
            rect5Over = false;
        }
        else if ( overRect(rectX2, rectY, rectSize, rectSize) ) {
            rect0Over = false;
            rect1Over = false;
            rect2Over = true;
            rect3Over = false;
            rect4Over = false;
            rect5Over = false;
        }

        else if ( overRect(rectX3, rectY, rectSize, rectSize) ) {
            rect0Over = false;
            rect1Over = false;
            rect2Over = false;
            rect3Over = true;
            rect4Over = false;
            rect5Over = false;
        }
        else if ( overRect(rectX4, rectY, rectSize, rectSize) ) {
            rect0Over = false;
            rect1Over = false;
            rect2Over = false;
            rect3Over = false;
            rect4Over = true;
            rect5Over = false;
        }
        else if ( overRect(rectX5, rectY, rectSize, rectSize) ) {
            rect0Over = false;
            rect1Over = false;
            rect2Over = false;
            rect3Over = false;
            rect4Over = false;
            rect5Over = true;
        }
        else {
            rect2Over = rect1Over = false;
        }
    }

    public void mousePressed() {
        if (rect0Over) {
            currentColor = rectColor;
            vis=0;
        }
        else if (rect1Over) {
            currentColor = rectColor;
            vis=1;
        }
        else if (rect2Over) {
            currentColor = rectColor;
            vis=2;
        }
        else if (rect3Over) {
            currentColor = rectColor;
            vis=3;
        }
        else if (rect4Over) {
            currentColor = rectColor;
            vis=4;
        }
        else if (rect5Over) {
            currentColor = rectColor;
            vis=5;
        }
    }

    public boolean overRect(int x, int y, int width, int height)  {
        if (mouseX >= x && mouseX <= x+width &&
                mouseY >= y && mouseY <= y+height) {
            return true;
        } else {
            return false;
        }
    }


    // A new Cell can be added to the ArrayList every cycle through draw().
    public void draw() {
        if(pause == false){
            timestep=timestep+1; //increase one time step
            background(255); //we paint the background white
            textSize(12);                 // STEP 4 Specify font to be used
            fill(0);                        // STEP 5 Specify font color
            float daystep=updateCycles;
            text(updateCycles,20,20);// time elapse and where to be displayed
            text("days",20,35);
            text("Lineage",rectX0,rectY+2*rectSize);
            text("# Div",rectX1,rectY+2*rectSize);
            text("Age",rectX2,rectY+2*rectSize);
            text("Telomeres",rectX3,rectY+2*rectSize);
            text("Mutations",rectX4,rectY+2*rectSize);
            text("Cell type",rectX5,rectY+2*rectSize);
            text("WNT",wntX,wntY+rectSize);
            text("EGF",egfX,wntY+rectSize);
            image(img1,55,bottom+10);

            //GUI box
            if(GUI_On ==true){
                noFill();
                stroke(0);
                rect(650, 28, 500, 740);
                rect(660, 595, 480, 155);
                rect(660, 100, 480, 465);
                fill(0);
                textSize(18);
                text("SIMULATION CONTROLS",655, 25);
                textSize(14);
                text("MUTATIONS",665, 590);
                text("SETUP",665, 95);
                textSize(12);
                if(pause == true){text("Paused",910, 68);}
                else {text("Running",910, 68);}
            }

            //draw the input buttons
            update(mouseX, mouseY);

            if (vis == 0) {
                fill(rectHighlight);
            } else {
                fill(rectColor);
            }
            stroke(255);
            rect(rectX0, rectY, rectSize, rectSize);

            if (vis == 1) {
                fill(rectHighlight);
            } else {
                fill(rectColor);
            }
            stroke(255);
            rect(rectX1, rectY, rectSize, rectSize);

            if (vis == 2) {
                fill(rectHighlight);
            } else {
                fill(rectColor);
            }
            stroke(0);
            stroke(255);
            rect(rectX2, rectY, rectSize, rectSize);

            if (vis == 3) {
                fill(rectHighlight);
            } else {
                fill(rectColor);
            }
            stroke(0);
            stroke(255);
            rect(rectX3, rectY, rectSize, rectSize);

            if (vis == 4) {
                fill(rectHighlight);
            } else {
                fill(rectColor);
            }
            stroke(0);
            stroke(255);
            rect(rectX4, rectY, rectSize, rectSize);

            if (vis == 5) {
                fill(rectHighlight);
            } else {
                fill(rectColor);
            }
            stroke(0);
            stroke(255);
            rect(rectX5, rectY, rectSize, rectSize);
            fill(rectColor);

            //EGF & WNT controls
            rect(wntX, wntY, rectSize, wntSize);
            rect(egfX, egfY, rectSize, egfSize);

            // testing
            Random rnd = new Random();
            if (testing == true) {
                randomSeed(1234);
                rnd = new Random(1234);
            }
            else {
                rnd = new Random();
            }

            if (cellBag==0){
                cellBag = cells.size()-1;//start counting again
                Collections.shuffle(cells, rnd);
                updateCycles=updateCycles+0.25f;
            }

            int rand = cellBag;//we get a random cell from the bag
            cellBag=cellBag-1;//remove the element from the bag
            int dispX =0; //we initialize the variables to store the location of the cell
            int dispY =0;
            int treeProgeny =0;
            int state=0;
            float age= 0;
            int telomere =0;
            int mutation0 = 0;
            int mutation1 = 0;
            int mutation2 = 0;
            int mutation3 = 0;
            int mutation4 = 0;
            int mutation5 = 0;
            int mutation6 = 0;
            int copyNumber0 = 0;
            int copyNumber1 = 0;
            int copyNumber2 = 0;
            int copyNumber3 = 0;
            int copyNumber4 = 0;
            int copyNumber5 = 0;
            int copyNumber6 = 0;

            //arrays that keep track of the cells at the bottom

// We get a random cell from the list (cells.size()-1)
            Cell q = (Cell) cells.get(rand);
            //We then go through the cell's genome (death, division)
            q.aging();
            //q.internalGenome(); //not used for now
            //q.mutation();  //not used for now

            //This is Contact Inhibition. If the cells surpass the carrying capacity of the tissue, they start to die
            if (cells.size()>(carrying_cap) && q.isDead()==true){


                //We get the position of the cell that is about to die
                dispX = q.divisionX();// q.divisionX is the variable that holds the X position in Cell
                dispY = q.divisionY();// q.divisionY is the variable that holds the Y position in Cell
                treeProgeny = q.divisionTree();// this keeps track of the progeny
                telomere=q.telomere();//returns the telomere number
                boolean neighbourExists = false; //checking that the chosen neighbour to copy exists

                int death_prob=0;
                if(dispY<=egfTotal)//cells going out of the canvas
                {death_prob=1;}
                else if(dispY<=SCClimit-r && dispY>egfTotal)//Only for TAC & suprabasal SCC cells, there's a chance to replace the neighbor
                {death_prob=PApplet.parseInt(random(1,4));}
                else if(dispY==SCClimit)//Only for basal SCC cells, there's a chance to replace the neighbor
                {death_prob=PApplet.parseInt(random(4,7));}


                //Case I- we get the left neighbor to "divide" by copiying the identity of the neighbor into the cell
                else if(death_prob==2){

                    int findleft=dispX-r;
                    int neighborTree=0;
                    int neighborState=0;
                    float neighborAge=0;
                    int neighborTelomere=0;

                    if(findleft<xstart*r){findleft=xend*r;}//to handle going left


                    //Then we find a neighbor and copy it's identity so that the gap is covered
                    for (int i=cells.size()-1; i>=0; i=i-1 ) { //This used to be a bug that left a gap when i>0
                        Cell p = (Cell) cells.get(i);

                        if (p.divisionX()==findleft && p.divisionY()==dispY) {
                            neighborTree= q.divisionTree();
                            neighborState=q.divisionState();
                            neighborAge=q.divisionAge();
                            neighborTelomere=q.telomere();
                            mutation0 = q.mutationsNumbers(0);
                            mutation1 = q.mutationsNumbers(1);
                            mutation2 = q.mutationsNumbers(2);
                            mutation3 = q.mutationsNumbers(3);
                            mutation4 = q.mutationsNumbers(4);
                            mutation5 = q.mutationsNumbers(5);
                            mutation6 = q.mutationsNumbers(6);
                            copyNumber0 = q.copyNumbers(0);
                            copyNumber1 = q.copyNumbers(1);
                            copyNumber2 = q.copyNumbers(2);
                            copyNumber3 = q.copyNumbers(3);
                            copyNumber4 = q.copyNumbers(4);
                            copyNumber5 = q.copyNumbers(5);
                            copyNumber6 = q.copyNumbers(6);

                        }

                    }

                    if (neighborTree != 0){
                        cells.remove(rand);
                        cells.add(new Cell(dispX,dispY,neighborTree, neighborState, neighborAge, 1, neighborTelomere,
                                mutation0, mutation1, mutation2, mutation3, mutation4, mutation5, mutation6,
                                copyNumber0, copyNumber1, copyNumber2, copyNumber3, copyNumber4, copyNumber5, copyNumber6));
                        neighbourExists = true;
                    }
                }


                //Case II- we get the right neighbor to divide
                else if(death_prob==3){
                    int findRight=dispX+r;
                    int neighborTree=0;
                    int neighborState=0;
                    float neighborAge=0;
                    int neighborTelomere=0;

                    if(findRight>xend*r){findRight=xstart*r;}//to handle going right
                    //Then we find a neighbor and copy it's identity so that the gap is covered
                    for (int i=cells.size()-1; i>=0; i=i-1 ) { //This used to be a bug htat left a gap when i>0
                        Cell p = (Cell) cells.get(i);
                        if (p.divisionX()==findRight && p.divisionY()==dispY) {
                            neighborTree= q.divisionTree();
                            neighborState=q.divisionState();
                            neighborAge=q.divisionAge();
                            neighborTelomere=q.telomere();
                            mutation0 = q.mutationsNumbers(0);
                            mutation1 = q.mutationsNumbers(1);
                            mutation2 = q.mutationsNumbers(2);
                            mutation3 = q.mutationsNumbers(3);
                            mutation4 = q.mutationsNumbers(4);
                            mutation5 = q.mutationsNumbers(5);
                            mutation6 = q.mutationsNumbers(6);
                            copyNumber0 = q.copyNumbers(0);
                            copyNumber1 = q.copyNumbers(1);
                            copyNumber2 = q.copyNumbers(2);
                            copyNumber3 = q.copyNumbers(3);
                            copyNumber4 = q.copyNumbers(4);
                            copyNumber5 = q.copyNumbers(5);
                            copyNumber6 = q.copyNumbers(6);
                        }
                    }
                    if (neighborTree != 0){
                        cells.remove(rand);
                        cells.add(new Cell(dispX,dispY,neighborTree, neighborState, neighborAge, 1, neighborTelomere,
                                mutation0, mutation1, mutation2, mutation3, mutation4, mutation5, mutation6,
                                copyNumber0, copyNumber1, copyNumber2, copyNumber3, copyNumber4, copyNumber5, copyNumber6));
                        neighbourExists = true;
                    }


                }

                //Case III- we remove it and pull cells downwards
                if(death_prob==1 || (death_prob==2 && neighbourExists == false) ||(death_prob==3 && neighbourExists == false) ){
                    cells.remove(rand);
                    //Then we update the location of all of the other cells so that the gap is covered
                    for (int i=cells.size()-1; i>=0; i=i-1 ) {
                        Cell p = (Cell) cells.get(i);
                        if (p.divisionX()==dispX && p.divisionY()<dispY) {
                            p.pulledY();} // If a cell dies, we pull the other cells on top of it to cover the gap
                    }
                }


                //Case IV- remove basal SCC, pull down a column
                else if(death_prob==4){
                    int SCCpull= dispX+(PApplet.parseInt(random(0,kColumns))*r);//Calculate which colum is going down.
                    if(SCCpull>sccEnd*r){SCCpull=sccStart*r;}
                    cells.remove(rand);
                    //Then we update the location of all of the other cells so that the gap is covered
                    for (int i=cells.size()-1; i>=0; i=i-1 ) {
                        Cell p = (Cell) cells.get(i);
                        if (p.divisionX()==SCCpull && p.divisionY()<dispY) {
                            p.pulledYSSC(dispX);} // If a cell dies, we pull the other cells on top of it to cover the gap
                    }
                }

                //Case V- have left neighbor divide in SCC
                else if(death_prob==5){
                    int findleft=dispX-r*kColumns;
                    int neighborTree=0;
                    int neighborState=0;
                    float neighborAge=0;
                    int neighborTelomere=0;

                    if(findleft<sccStart*r){findleft=sccEnd*r;}//to handle going left
                    //Then we find a neighbor and copy it's identity so that the gap is covered
                    for (int i=cells.size()-1; i>=0; i=i-1 ) { //This used to be a bug htat left a gap when i>0
                        Cell p = (Cell) cells.get(i);
                        if (p.divisionX()==findleft && p.divisionY()==SCClimit) {
                            neighborTree= q.divisionTree();
                            neighborState=q.divisionState();
                            neighborAge=q.divisionAge();
                            neighborTelomere=q.telomere();
                            mutation0 = q.mutationsNumbers(0);
                            mutation1 = q.mutationsNumbers(1);
                            mutation2 = q.mutationsNumbers(2);
                            mutation3 = q.mutationsNumbers(3);
                            mutation4 = q.mutationsNumbers(4);
                            mutation5 = q.mutationsNumbers(5);
                            mutation6 = q.mutationsNumbers(6);
                            copyNumber0 = q.copyNumbers(0);
                            copyNumber1 = q.copyNumbers(1);
                            copyNumber2 = q.copyNumbers(2);
                            copyNumber3 = q.copyNumbers(3);
                            copyNumber4 = q.copyNumbers(4);
                            copyNumber5 = q.copyNumbers(5);
                            copyNumber6 = q.copyNumbers(6);
                        }
                    }
                    cells.remove(rand);
                    cells.add(new Cell(dispX,dispY,neighborTree, neighborState, neighborAge, 1, neighborTelomere,
                            mutation0, mutation1, mutation2, mutation3, mutation4, mutation5, mutation6,
                            copyNumber0, copyNumber1, copyNumber2, copyNumber3, copyNumber4, copyNumber5, copyNumber6));
                }

                //Case VI- have right neighbor divide
                else if(death_prob==6){
                    int findright=dispX+r*kColumns;
                    int neighborTree=0;
                    int neighborState=0;
                    float neighborAge=0;
                    int neighborTelomere=0;

                    if(findright>sccEnd*r){findright=sccStart*r;}//to handle going left
                    //Then we find a neighbor and copy it's identity so that the gap is covered
                    for (int i=cells.size()-1; i>=0; i=i-1 ) { //This used to be a bug that left a gap when i>0
                        Cell p = (Cell) cells.get(i);
                        if (p.divisionX()==findright && p.divisionY()==SCClimit) {
                            neighborTree= q.divisionTree();
                            neighborState=q.divisionState();
                            neighborAge=q.divisionAge();
                            neighborTelomere=q.telomere();
                            mutation0 = q.mutationsNumbers(0);
                            mutation1 = q.mutationsNumbers(1);
                            mutation2 = q.mutationsNumbers(2);
                            mutation3 = q.mutationsNumbers(3);
                            mutation4 = q.mutationsNumbers(4);
                            mutation5 = q.mutationsNumbers(5);
                            mutation6 = q.mutationsNumbers(6);
                            copyNumber0 = q.copyNumbers(0);
                            copyNumber1 = q.copyNumbers(1);
                            copyNumber2 = q.copyNumbers(2);
                            copyNumber3 = q.copyNumbers(3);
                            copyNumber4 = q.copyNumbers(4);
                            copyNumber5 = q.copyNumbers(5);
                            copyNumber6 = q.copyNumbers(6);
                        }
                    }
                    cells.remove(rand);
                    cells.add(new Cell(dispX,dispY,neighborTree, neighborState, neighborAge, 1, neighborTelomere,
                            mutation0, mutation1, mutation2, mutation3, mutation4, mutation5, mutation6,
                            copyNumber0, copyNumber1, copyNumber2, copyNumber3, copyNumber4, copyNumber5, copyNumber6));
                }
                //else {println("do not die?");}

            }

            // If it doesn't die, it has the chance to divide
            else if (q.divide()==true){//this checks for it's chances of division

                dispX = q.divisionX();
                dispY = q.divisionY();
                state = q.divisionState();
                treeProgeny = q.divisionTree();// this keeps track of the progeny
                telomere=q.telomere();//returns the telomere number
                mutation0 = q.mutationsNumbers(0);
                mutation1 = q.mutationsNumbers(1);
                mutation2 = q.mutationsNumbers(2);
                mutation3 = q.mutationsNumbers(3);
                mutation4 = q.mutationsNumbers(4);
                mutation5 = q.mutationsNumbers(5);
                mutation6 = q.mutationsNumbers(6);
                copyNumber0 = q.copyNumbers(0);
                copyNumber1 = q.copyNumbers(1);
                copyNumber2 = q.copyNumbers(2);
                copyNumber3 = q.copyNumbers(3);
                copyNumber4 = q.copyNumbers(4);
                copyNumber5 = q.copyNumbers(5);
                copyNumber6 = q.copyNumbers(6);

                int divY=PApplet.parseInt ((SCClimit-dispY)/r);  // to store division per row

                if (dispY<=SCClimit && dispY>=wntTotal){ //If the cell is a stem cell don't age and count it //If the cell is a stem cell don't age and count it
                    divNoSCC=divNoSCC+1;  //keeps track of divisions
                    divisionYtracker[divY]=divisionYtracker[divY]+1;
                    age=0;
                    //println("True2");
                }
                else if (dispY<wntTotal && dispY>=egfTotal){//TAC cells
                    divNoTAC=divNoTAC+1;  //keeps track of divisions
                    divisionYtracker[divY]=divisionYtracker[divY]+1;
                    age= q.divisionAge(); // how many times have you been picked up
                    //println("True3");
                }
                else if (dispY<egfTotal) {
                    //divNoTAC=divNoTAC+1;  //keeps track of divisions
                    age= q.divisionAge(); // how many times have you been picked up
                    //println("True4");
                }
                //else {println("ERROR");}

                //All the possible movements in cell division
                int disp=(int)random(0,101);//Random number between 0 and 100
                //normal upwards displacement
                int rx=0;
                int ry=0;

                //Chance of lateral cell divisions can be changed here
                //Movement of TAC Compartment . Order is Important here.
                if(dispY<=SCClimit-2*r){
                    TACMoveLU = 12 - 2*(12*q.sideDiv() - 12)*12/100;//12% chance of lateral left & up displacement
                    TACMoveRU = 12 - 2*(12*q.sideDiv() - 12)*12/100;//12% chance of lateral right & up displacement
                    TACMoveL = 12*q.sideDiv();//12% chance of lateral left displacement
                    TACMoveR = 12*q.sideDiv();//12% chance of lateral right displacement
                    TACMoveLD = 12 - 2*(12*q.sideDiv() - 12)*12/100;//12% chance of lateral left & down displacement
                    TACMoveRD = 12 - 2*(12*q.sideDiv() - 12)*12/100;//12% chance of lateral right & down displacement
                    TACMoveU = 20 - 2*(12*q.sideDiv() - 12)*20/100;  //20% chance of up displacement (remaining to down)
                    TACMoveLU_C = TACMoveLU; //cumulative
                    TACMoveRU_C = TACMoveLU_C + TACMoveRU;//cumulative
                    TACMoveL_C = TACMoveRU_C + TACMoveL;//cumulative
                    TACMoveR_C = TACMoveL_C + TACMoveR;//cumulative
                    TACMoveLD_C = TACMoveR_C + TACMoveLD;//cumulative
                    TACMoveRD_C = TACMoveLD_C + TACMoveRD ;//cumulative
                    TACMoveU_C = TACMoveRD_C + TACMoveU;//cumulative

                    if (disp>=0 && disp<TACMoveLU){ rx=-1*r;  ry=-1*r;}//12% chance of lateral left & up displacement
                    else if (disp>=TACMoveLU_C && disp<TACMoveRU_C){ rx=1*r;  ry=-1*r;}//12% chance of lateral right & up displacement
                    else if (disp>=TACMoveRU_C && disp<TACMoveL_C){ rx=-1*r;  ry=0;}//12% chance of lateral left displacement
                    else if (disp>=TACMoveL_C && disp<TACMoveR_C){ rx=1*r;  ry=0;}//12% chance of lateral right displacement
                    else if (disp>=TACMoveR_C && disp<TACMoveLD_C){ rx=-1*r;  ry=1*r;}//12% chance of lateral left & down displacement
                    else if (disp>=TACMoveLD_C && disp<TACMoveRD_C){ rx=1*r;  ry=1*r;}//12% chance of lateral right & down displacement
                    else if (disp>=TACMoveRD_C && disp<TACMoveU_C) { rx=0;  ry=-1*r;} //20% chance of up displacement
                    else if (disp>=TACMoveU_C && disp<=100){ rx=0;  ry=1*r;} //6% chance of down displacement
                    //else {println("ERROR");}

                    //To handle going to the left/right of the crypt
                    if (dispX+rx<xstart*r){dispX=xend*r;}    //going from left to right
                    else if (dispX+rx>xend*r){dispX=xstart*r;}  //going from right to left
                    else {dispX=dispX+rx;} //if it's in the middle
                }

                //Movement of suprabasal SC Compartment
                if(dispY==SCClimit-r){
                    SBMoveLU = 12 - 2*(12*q.sideDiv() - 12)*20/100;//12% chance of lateral left & up displacement
                    SBMoveRU = 12 - 2*(12*q.sideDiv() - 12)*20/100;//12% chance of lateral right & up displacement
                    SBMoveL = 12*q.sideDiv();//12% chance of lateral left displacement
                    SBMoveR = 12*q.sideDiv(); //12% chance of lateral right displacement
                    SBMoveU = 53 - 2*(12*q.sideDiv() - 12)*53/100; //46% chance of up displacement (remaining % goes to down)
                    SBMoveLU_C = SBMoveLU; //cumulative
                    SBMoveRU_C = SBMoveLU_C + SBMoveRU;//cumulative
                    SBMoveL_C = SBMoveRU_C + SBMoveL;//cumulative
                    SBMoveR_C = SBMoveL_C + SBMoveR;//cumulative
                    SBMoveU_C = SBMoveR_C + SBMoveU;//cumulative

                    if (disp>=0 && disp<SBMoveLU_C){ rx=-1*r;  ry=-1*r;}//12% chance of lateral left & up displacement
                    else if (disp>=SBMoveLU_C && disp<SBMoveRU_C){ rx=1*r;  ry=-1*r;}//12% chance of lateral right & up displacement
                    else if (disp>=SBMoveRU_C && disp<SBMoveL_C){ rx=-1*r;  ry=0;}//12% chance of lateral left displacement
                    else if (disp>=SBMoveL_C && disp<SBMoveR_C){ rx=1*r;  ry=0;}//12% chance of lateral right displacement
                    else if (disp>=SBMoveR_C && disp<SBMoveU_C) { rx=0;  ry=-1*r;} //46% chance of up displacement
                    else if (disp>=SBMoveU_C && disp<=101){ //6% chance of downward displacement NOT USED 4 NOW
                        int backtoc=floor((float)(dispX-xstart)/(r*kColumns));
                        if(backtoc>=sccNo){backtoc=0;}
                        dispX=stemXtracker[backtoc]; ry=1*r;} //puts the cell in one of the previously calculated places for stem cells
                    //else {println("ERROR");}

                    //To handle going to the left/right of the crypt
                    if (dispX+rx<xstart*r){dispX=xend*r;}    //going from left to right
                    else if (dispX+rx>xend*r){dispX=xstart*r;}  //going from right to left
                    else {dispX=dispX+rx;} //if it's in the middle
                }

                //movement of basal SCcompartment
                if(dispY==SCClimit){
                    int sscDisp=(int)random(0, kColumns);
                    SCCMoveL = 12*q.sideDiv(); //testVariable[run]*q.sideDiv();//12% chance of lateral left displacement (remainder to right displacement)
                    SCCMoveU = 100 - SCCMoveL*2;//76% chance of up & random side displacement
                    SCCMoveU_C = SCCMoveU; //cumulative
                    SCCMoveL_C = SCCMoveU_C + SCCMoveL;//cumulative

                    if (disp>=0 && disp<SCCMoveU_C){ rx=-sscDisp*r;  ry=-1*r;}// 76% chance of up and random sideways displacement
                    else if (disp>=SCCMoveU_C && disp<SCCMoveL_C){ rx=-1*r*kColumns;  ry=0;}//12% chance of lateral left displacement
                    else { rx=1*r*kColumns;  ry=0;}//12% chance of lateral right displacement}


                    //To handle going to the left/right of the crypt
                    if (dispX+rx<xstart*r){dispX=sccEnd*r;}    //going from left to right
                    else if (dispX+rx>xend*r){dispX=sccStart*r;}  //going from right to left
                    else {dispX=dispX+rx;} //if it's in the middle
                }

                dispY=dispY+ry;
                //update all of the other cells
                for (int i=cells.size()-1; i>=0; i=i-1 ) {
                    Cell p = (Cell) cells.get(i);
                    if (p.divisionX()==dispX && p.divisionY()<=dispY) {
                        p.pushedY();}    // We push all the other cells on top of the dividing cell to make room for the new one
                }

                //Then we add the new cell on the open space
                cells.add(new Cell(dispX,dispY,treeProgeny, state, age, 1, telomere,
                        mutation0, mutation1, mutation2, mutation3, mutation4, mutation5, mutation6,
                        copyNumber0, copyNumber1, copyNumber2, copyNumber3, copyNumber4, copyNumber5, copyNumber6)); //We flag it's state as 1 (recently divided)
                //chance of Chromosome missegregation should be implemented here
                //we need to add information of missegregation when creating a new cell. It should be an identical copy

            }

            //If it doesn't die or divide
            //else {println("do nothing");}

            // We then iterate through our ArrayList and get each cell's possition to display it
            // The ArrayList keeps track of the total number of cells.
            int m=0;
            mutantCount = 0;
            SCCmutantCount = 0;
            for (int i=cells.size()-1; i>=0; i=i-1 ) {
                Cell p = (Cell) cells.get(i);
                p.display();
                //To Count Telomeres and cell types
                if(p.divisionY()>=wntTotal){
                    //p.telomere=telomereLength;
                    teloCountSCC=teloCountSCC+p.telomere();//add all the telomeres in SCC
                    countSCC=countSCC+1;}//counts SC Cell number
                else{teloCountTAC=teloCountTAC+p.telomere();//add all the telomeres in TAC
                    countTAC=countTAC+1;} //counts TA+Epithelial Cell number

                int g0=0; //we initialize the variables to store the location of the cell
                g0=p.divisionY();


                if (g0==SCClimit && m<sccNo)
                {
                    stemMtracker[m]=p.divisionTree(); // It's trying to access an extra element. I think it's a new element from sidemovement...

                    m=m+1;}

                if (firstExtinct == false){
                    if(p.totalMutations() != 0){
                        mutantCount = mutantCount +1;
                    }}

                if (g0==SCClimit){
                    if(p.totalMutations() != 0){
                        SCCmutantCount = SCCmutantCount +1;
                    }}

                //count cells of different types
                if (p.divisionTree() == 1){WT = WT+1;}
                else if (p.divisionTree() == 2){mutant1 = mutant1+1;}
                else if (p.divisionTree() == 3){mutant2 = mutant2+1;}
                else if (p.divisionTree() == 4){mutant3 = mutant3+1;}
                else if (p.divisionTree() == 5){mutant4 = mutant4+1;}
                else if (p.divisionTree() == 6){mutant5 = mutant5+1;}
                else if (p.divisionTree() == 7){mutant6 = mutant6+1;}
                else if (p.divisionTree() == 8){mutant7 = mutant7+1;}
                else if (p.divisionTree() == 9){mutant8 = mutant8+1;}
                else if (p.divisionTree() ==10){mutant9 = mutant9+1;}
                else if (p.divisionTree() == 11){mutant10 = mutant10+1;}

            }


            if(firstExtinct ==false && mutantCount == 0 && daystep !=1){
                firstExtinctTime = daystep;
                firstExtinct = true;}

            //teloCountSCC=int(teloCountSCC/cells.size());
            //Print to File
            String logging_part2 = "";
            for (int i=0;i<divisionYtracker.length;i++){
                logging_part2 = logging_part2 + str(divisionYtracker[i]) + ", ";
            }

            String logging_part3 = "";
            for (int i=0;i<mutationHistory.length;i++){
                logging_part3 = logging_part3 + str(mutationHistory[i]) + ", ";
            }

            String logging_part4 =
                    str(sheddingHeight) + ", " +
                            str(endsim) + ", " +
                            str(wntLevel) + ", " +
                            str(egfLevel) + ", " +
                            str(cryptWidth) + ", " +
                            str(cryptLength) + ", " +
                            str(sccNo) + ", " +
                            str(telomereLength) + ", " +
                            str(copyIncreaseImpact) + ", " +
                            str(copyDecreaseImpact) + ", " +
                            str(copyIncreaseImpactAPC) + ", " +
                            str(copyDecreaseImpactAPC) + ", " +
                            str(minCellCycle) + ", " +
                            str(maxCellCycle) + ", " +
                            str(thresholdCTNNB1) + ", " +
                            str(thresholdCMYC) + ", " +
                            str(timeend) + ", " +
                            str(TACMoveLU) + ", " +
                            str(TACMoveRU) + ", " +
                            str(TACMoveL) + ", " +
                            str(TACMoveR) + ", " +
                            str(TACMoveLD) + ", " +
                            str(TACMoveRD) + ", " +
                            str(TACMoveU) + ", " +
                            str(SBMoveLU) + ", " +
                            str(SBMoveRU) + ", " +
                            str(SBMoveL) + ", " +
                            str(SBMoveR) + ", " +
                            str(SBMoveU) + ", " +
                            str(SCCMoveU) + ", " +
                            str(SCCMoveL);

            String logging_part5 = WT + ", " + mutant1 + ", " + mutant2  + ", " + mutant3  + ", " + mutant4  + ", " +
                    mutant5  + ", " + mutant6  + ", " + mutant7  + ", " + mutant8  + ", " + mutant9  + ", " + mutant10;


            String logging= str(daystep)+", "+str(divNoSCC)+", "+str(divNoSCC/(daystep+1))+", "+str(divNoTAC)
                    +", "+str(divNoTAC/(daystep+1))+", "+str(teloCountSCC)+", "+str(countSCC)+", "+str(teloCountSCC/countSCC)
                    +", "+str(teloCountTAC)+", "+str(countTAC)+", "+str(teloCountTAC/countTAC)+", " + str(firstMonoTime) +", "
                    + str(firstExtinctTime) +", " + mutantCount + ", " + SCCmutantCount + ", "+ logging_part2 + ", " + logging_part3 + ", " + logging_part4
                    + ", " + logging_part5
                    ;
            textFile.println(logging);
            //println("endday");

            teloCountSCC=0;
            countSCC=0;
            teloCountTAC=0;
            countTAC=0;
            WT = 0;      //# cells of wild type
            mutant1 = 0; //# cells of mutant type x
            mutant2 = 0; //# cells of mutant type x
            mutant3 = 0; //# cells of mutant type x
            mutant4 = 0; //# cells of mutant type x
            mutant5 = 0; //# cells of mutant type x
            mutant6 = 0; //# cells of mutant type x
            mutant7 = 0; //# cells of mutant type x
            mutant8 = 0; //# cells of mutant type x
            mutant9 = 0; //# cells of mutant type x
            mutant10 = 0; //# cells of mutant type x

            //mutations from GUI
            if(GUI_On ==true) {
                if (mutateNext == true){
                    int a1 = PApplet.parseInt(t1.getValue());
                    int a2 = PApplet.parseInt(t2.getValue());
                    int a3 = PApplet.parseInt(t3.getValue());
                    int a4 = PApplet.parseInt(t4.getValue());
                    int a5 = PApplet.parseInt(t5.getValue());
                    int a6 = PApplet.parseInt(t6.getValue());
                    int a7 = PApplet.parseInt(t7.getValue());
                    int a8 = PApplet.parseInt(t8.getValue());
                    int a9 = PApplet.parseInt(t9.getValue());
                    int a10 = PApplet.parseInt(t10.getValue());
                    int a11 = PApplet.parseInt(t11.getValue());
                    int a12 = PApplet.parseInt(t12.getValue());
                    int a13 = PApplet.parseInt(t13.getValue());
                    int a14 = PApplet.parseInt(t14.getValue());
                    int selectedStemCell = PApplet.parseInt(random(sccNo));
                    int xPos = xstart*r + r*kColumns*selectedStemCell;
                    for (int j=cells.size()-1; j>=0; j=j-1 ) {

                        Cell t = (Cell) cells.get(j);
                        dispX = t.divisionX();
                        dispY = t.divisionY();
                        if (dispY==SCClimit && dispX==xPos)
                        {
                            t.mutateGUI(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14);

                            delayCount=0;
                            mutationHistory[nextPosition] = updateCycles;
                            for (int k = 1; k<t.mutations.length;k++){
                                mutationHistory[nextPosition+k] = t.mutations[k-1];
                            }
                            for (int l = 1; l<=t.copyNumber.length;l++){
                                mutationHistory[nextPosition+t.mutations.length +l] = t.copyNumber[l-1];
                            }
                            nextPosition = nextPosition + t.mutations.length + t.copyNumber.length;
                        }
                    }
                    mutateNext = false;
                }
            }

            //monoclonlaity simulation end- take a pic every time it becomes monoclonal
            int simmilar=0;
            if (endsim==0){
                for (int i=0; i<sccNo-1; i++) {
                    if (stemMtracker[i] == stemMtracker[i+1])
                        simmilar=simmilar+1;
                    if (simmilar==sccNo-1){
                        captureImage(); //Save image
                        textFile.flush();
                        textFile.close();

                        loggerObject.log(logging); //write the day on Alldata

                        timestep=1;//reset simulation
                        updateCycles=1;
                        divNoSCC=0; // used for the average of division in SCC
                        divNoTAC=0;
                        for (int j =0; j<mutationHistory.length;j++){
                            mutationHistory[j] = 0;}
                        for (int l=0; l<=cryptLength; l++){
                            divisionYtracker[l]=0;
                        }
                        WT = 0;      //# cells of wild type
                        mutant1 = 0; //# cells of mutant type x
                        mutant2 = 0; //# cells of mutant type x
                        mutant3 = 0; //# cells of mutant type x
                        mutant4 = 0; //# cells of mutant type x
                        mutant5 = 0; //# cells of mutant type x
                        mutant6 = 0; //# cells of mutant type x
                        mutant7 = 0; //# cells of mutant type x
                        mutant8 = 0; //# cells of mutant type x
                        mutant9 = 0; //# cells of mutant type x
                        mutant10 = 0; //# cells of mutant type x
                        setup();
                    }
                }
            }

            //timed simmulation end - take a pic every time time reaches endsim

            if(endsim>=1 && GUI_On == false){

                //monoclonality mutation
                simmilar=0;


                for (int i=0; i<sccNo-1; i++) {
                    if (stemMtracker[i] == stemMtracker[i+1]){
                        simmilar=simmilar+1;}
                    if (simmilar==sccNo-1){
                        if(firstMono == false && daystep >1){
                            firstMonoTime = daystep;
                            firstMono = true;}

                        //when we reach monoclonality and 7 days since last stem cell became similar,
                        //need to pick a random stem cell,
                        //pick a random mutation,
                        //and then change the genome properties of the cell and the stem cell lineage to a new number
                        if (lastDay != daystep) {
                            delayCount = delayCount + 0.25f;
                            lastDay = daystep;}

                        int selectedStemCell = PApplet.parseInt(random(sccNo));
                        int xPos = xstart*r + r*kColumns*selectedStemCell;
                        for (int j=cells.size()-1; j>=0; j=j-1 ) {
                            Cell p = (Cell) cells.get(j);
                            dispX = p.divisionX();
                            dispY = p.divisionY();

                            if (dispY==SCClimit && dispX==xPos && daystep ==1 && day1Mutation ==false)
                            {
                                mutantValue = runList[run];
                                p.mutateNormal();
                                delayCount = 0;
                                day1Mutation = true;
                                mutationHistory[nextPosition] = daystep;
                                for (int k = 1; k<=p.mutations.length;k++){
                                    mutationHistory[nextPosition+k] = p.mutations[k-1];
                                }
                                for (int l = 1; l<=p.copyNumber.length;l++){
                                    mutationHistory[nextPosition+p.mutations.length +l] = p.copyNumber[l-1];
                                }
                                nextPosition = nextPosition + p.mutations.length + p.copyNumber.length + 1;
                                captureImage(); //Save image
                            }

                            else if (dispY==SCClimit && dispX==xPos && daystep!=1 && delayCount >=200)
                            {
                                mutantValue = runList[run];
                                p.mutateNormal();
                                delayCount=0;
                                mutationHistory[nextPosition] = daystep;
                                for (int k = 1; k<p.mutations.length;k++){
                                    mutationHistory[nextPosition+k] = p.mutations[k-1];
                                }
                                for (int l = 1; l<=p.copyNumber.length;l++){
                                    mutationHistory[nextPosition+p.mutations.length +l] = p.copyNumber[l-1];
                                }
                                nextPosition = nextPosition + p.mutations.length + p.copyNumber.length;
                                captureImage(); //Save image
                            }

                        }

                    }

                }

                if (daystep==timeend || firstMono ==true) {
                    for (int j=cells.size()-1; j>=0; j=j-1 ) {
                        Cell p = (Cell) cells.get(j);
                        p.display();
                    }
                    captureImage(); //Save image
                    textFile.flush();
                    textFile.close();

                    loggerObject.log(logging); //write the day on Alldata
                    timestep=1;//reset simulation
                    updateCycles=1;
                    divNoSCC=0; // used for the average of division in SCC
                    divNoTAC=0;
                    delayCount = 0;
                    day1Mutation = false;
                    lastDay = 0;
                    firstMono = false;
                    firstExtinct = false;
                    firstMonoTime = 0;
                    firstExtinctTime = 0;
                    mutantCount = 0;
                    SCCmutantCount = 0;
                    mutationHistory = new float[200];
                    nextPosition = 0;
                    for (int l=0; l<=cryptLength; l++){
                        divisionYtracker[l]=0;
                    }
                    WT = 0;      //# cells of wild type
                    mutant1 = 0; //# cells of mutant type x
                    mutant2 = 0; //# cells of mutant type x
                    mutant3 = 0; //# cells of mutant type x
                    mutant4 = 0; //# cells of mutant type x
                    mutant5 = 0; //# cells of mutant type x
                    mutant6 = 0; //# cells of mutant type x
                    mutant7 = 0; //# cells of mutant type x
                    mutant8 = 0; //# cells of mutant type x
                    mutant9 = 0; //# cells of mutant type x
                    mutant10 = 0; //# cells of mutant type x
                    setup();
                    iteration = iteration + 1;
                    if (iteration > noIterations){
                        run = run+1;
                        iteration = 0;}
                }
            }

            if(endsim>=1 && GUI_On == true){

                if (daystep==timeend) {
                    captureImage(); //Save image
                    textFile.flush();
                    textFile.close();

                    loggerObject.log(logging); //write the day on Alldata
                    timestep=1;//reset simulation
                    updateCycles=1;
                    divNoSCC=0; // used for the average of division in SCC
                    divNoTAC=0;
                    delayCount = 0;
                    day1Mutation = false;
                    lastDay = 0;
                    firstMono = false;
                    firstExtinct = false;
                    firstMonoTime = 0;
                    firstExtinctTime = 0;
                    mutantCount = 0;
                    mutationHistory = new float[200];
                    nextPosition = 0;
                    for (int l=0; l<=cryptLength; l++){
                        divisionYtracker[l]=0;
                    }
                    WT = 0;      //# cells of wild type
                    mutant1 = 0; //# cells of mutant type x
                    mutant2 = 0; //# cells of mutant type x
                    mutant3 = 0; //# cells of mutant type x
                    mutant4 = 0; //# cells of mutant type x
                    mutant5 = 0; //# cells of mutant type x
                    mutant6 = 0; //# cells of mutant type x
                    mutant7 = 0; //# cells of mutant type x
                    mutant8 = 0; //# cells of mutant type x
                    mutant9 = 0; //# cells of mutant type x
                    mutant10 = 0; //# cells of mutant type x
                    setup();

                }
            }
        }
    }


    public void actionPerformed(GUIEvent e) {
        //if the mutate button is pressed
        if (e.getSource() == b1) {
            mutateNext = true;
        }
        //if the reset button is pressed
        else if (e.getSource() == b2) {
            reset();
        }
        //if the start/stop button is pressed when sim is paused
        else if (e.getSource() == b3 && pause == true) {
            pause = false;
            text("Running",910, 68);

            //pick up all the new assumption values
            sheddingHeight = PApplet.parseInt(a1.getValue());
            endsim = PApplet.parseInt(a2.getValue());
            wntLevel = PApplet.parseInt(a3.getValue());
            egfLevel = PApplet.parseInt(a4.getValue());
            cryptWidth = PApplet.parseInt(a5.getValue());
            cryptLength = PApplet.parseInt(a6.getValue());
            sccNo = PApplet.parseInt(a7.getValue());
            telomereLength = PApplet.parseInt(a8.getValue());
            copyIncreaseImpact = PApplet.parseInt(a9.getValue());
            copyDecreaseImpact = PApplet.parseInt(a10.getValue());
            copyIncreaseImpactAPC = PApplet.parseInt(a11.getValue());
            copyDecreaseImpactAPC = PApplet.parseInt(a12.getValue());
            minCellCycle = PApplet.parseInt(a13.getValue());
            maxCellCycle = PApplet.parseInt(a14.getValue());
            thresholdCTNNB1 = PApplet.parseInt(a15.getValue());
            thresholdCMYC = PApplet.parseInt(a16.getValue());
            timeend = PApplet.parseInt(a17.getValue());
            TACMoveLU = PApplet.parseInt(a18.getValue());
            TACMoveRU = PApplet.parseInt(a19.getValue());
            TACMoveL  = PApplet.parseInt(a20.getValue());
            TACMoveR  = PApplet.parseInt(a21.getValue());
            TACMoveLD  = PApplet.parseInt(a22.getValue());
            TACMoveRD  = PApplet.parseInt(a23.getValue());
            TACMoveU = PApplet.parseInt(a24.getValue());
            SBMoveLU  = PApplet.parseInt(a25.getValue());
            SBMoveRU  = PApplet.parseInt(a26.getValue());
            SBMoveL  = PApplet.parseInt(a27.getValue());
            SBMoveR  = PApplet.parseInt(a28.getValue());
            SBMoveU  = PApplet.parseInt(a29.getValue());
            SCCMoveU = PApplet.parseInt(a30.getValue());
            SCCMoveL = PApplet.parseInt(a31.getValue());

            //recalculate dependent variables
            stemXtracker= new int[sccNo];
            stemMtracker= new int[sccNo];
            divisionYtracker= new float[cryptLength+1];//to measure division along the Y axis
            kColumns=ceil((float)cryptWidth/sccNo);//
            carrying_cap= sccNo*kColumns*cryptLength; //this determines when death kicks in. It won't until the carrying capacity is surpased
            egfSize=(int)(-3*egfLevel);//
            wntSize=(int)(-3*wntLevel/10-14);// additional 14 to ensure bottom three layers are stem cells in base case
            xend=xstart+(sccNo*kColumns)-1;//space between x=0 and the end of the crypt
            wntTotal=wntY+wntSize;//same as Cell
            egfTotal=egfY+egfSize;//probably best to collapse these
            SCCMoveU_C = SCCMoveU; //cumulative
            SCCMoveL_C = SCCMoveU_C + SCCMoveL;//cumulative
            SBMoveLU_C = SBMoveLU; //cumulative
            SBMoveRU_C = SBMoveLU_C + SBMoveRU;//cumulative
            SBMoveL_C = SBMoveRU_C + SBMoveL;//cumulative
            SBMoveR_C = SBMoveL_C + SBMoveR;//cumulative
            SBMoveU_C = SBMoveR_C + SBMoveU;//cumulative
            TACMoveLU_C = TACMoveLU; //cumulative
            TACMoveRU_C = TACMoveLU_C + TACMoveRU;//cumulative
            TACMoveL_C = TACMoveRU_C + TACMoveL;//cumulative
            TACMoveR_C = TACMoveL_C + TACMoveR;//cumulative
            TACMoveLD_C = TACMoveR_C + TACMoveLD;//cumulative
            TACMoveRD_C = TACMoveLD_C + TACMoveRD ;//cumulative
            TACMoveU_C = TACMoveRD_C + TACMoveU;//cumulative

            timestep=1;//reset simulation
            updateCycles=1;
            divNoSCC=0; // used for the average of division in SCC
            divNoTAC=0;
            delayCount = 0;
            day1Mutation = false;
            lastDay = 0;
            firstMono = false;
            firstExtinct = false;
            firstMonoTime = 0;
            firstExtinctTime = 0;
            mutantCount = 0;
            mutationHistory = new float[200];
            nextPosition = 0;
            for (int l=0; l<=cryptLength; l++){
                divisionYtracker[l]=0;
            }
            WT = 0;      //# cells of wild type
            mutant1 = 0; //# cells of mutant type x
            mutant2 = 0; //# cells of mutant type x
            mutant3 = 0; //# cells of mutant type x
            mutant4 = 0; //# cells of mutant type x
            mutant5 = 0; //# cells of mutant type x
            mutant6 = 0; //# cells of mutant type x
            mutant7 = 0; //# cells of mutant type x
            mutant8 = 0; //# cells of mutant type x
            mutant9 = 0; //# cells of mutant type x
            mutant10 = 0; //# cells of mutant type x
            setup();
        }
        //if the start/stop button is pressed when sim is running
        else if (e.getSource() == b3 && pause == false) {
            pause = true;
            fill(255);
            stroke(255);
            rect(910, 68-15,60,17);
            fill(0);
            textSize(12);
            text("Paused",910, 68);
        }
        //if photo button is pressed
        else if (e.getSource() == b4) {
            captureImage(); //Save image
        }

    }

    //reset the simulation
    public void reset(){
        captureImage(); //Save image
        textFile.flush();
        textFile.close();

        timestep=1;//reset simulation
        updateCycles=1;
        divNoSCC=0; // used for the average of division in SCC
        divNoTAC=0;
        delayCount = 0;
        day1Mutation = false;
        lastDay = 0;
        firstMono = false;
        firstExtinct = false;
        firstMonoTime = 0;
        firstExtinctTime = 0;
        mutantCount = 0;
        mutationHistory = new float[200];
        nextPosition = 0;
        for (int l=0; l<=cryptLength; l++){
            divisionYtracker[l]=0;
        }
        pause = true;
        resetNow = true;
        WT = 0;      //# cells of wild type
        mutant1 = 0; //# cells of mutant type x
        mutant2 = 0; //# cells of mutant type x
        mutant3 = 0; //# cells of mutant type x
        mutant4 = 0; //# cells of mutant type x
        mutant5 = 0; //# cells of mutant type x
        mutant6 = 0; //# cells of mutant type x
        mutant7 = 0; //# cells of mutant type x
        mutant8 = 0; //# cells of mutant type x
        mutant9 = 0; //# cells of mutant type x
        mutant10 = 0; //# cells of mutant type x
        setup();
    }

    public void captureImage(){
        //redraw the parameters as the interfascia library seems to not refresh appropriately for the image capture
        int yLine1 = 650;
        int yLine2 = 700;
        int xCol1 = 670;
        int spacing = 20;

        noFill();
        stroke(0);
        rect(650, 28, 500, 740);
        rect(660, 595, 480, 155);
        rect(660, 100, 480, 465);
        fill(0);
        textSize(18);
        text("SIMULATION CONTROLS",655, 25);
        textSize(14);
        text("MUTATIONS",665, 590);
        text("SETUP",665, 95);
        textSize(12);
        text(PApplet.parseInt(mutationHistory[0]), xCol1, yLine1);
        text(PApplet.parseInt(mutationHistory[1]), xCol1 + 2*spacing, yLine1);
        text(PApplet.parseInt(mutationHistory[2]), xCol1 + 4*spacing, yLine1);
        text(PApplet.parseInt(mutationHistory[3]), xCol1 + 6*spacing, yLine1);
        text(PApplet.parseInt(mutationHistory[4]), xCol1 + 8*spacing, yLine1);
        text(PApplet.parseInt(mutationHistory[5]), xCol1 + 10*spacing, yLine1);
        text(PApplet.parseInt(mutationHistory[6]), xCol1 + 12*spacing, yLine1);
        text(PApplet.parseInt(mutationHistory[7]), xCol1, yLine2);
        text(PApplet.parseInt(mutationHistory[8]), xCol1 + 2*spacing, yLine2);
        text(PApplet.parseInt(mutationHistory[9]), xCol1 + 4*spacing, yLine2);
        text(PApplet.parseInt(mutationHistory[10]), xCol1 + 6*spacing, yLine2);
        text(PApplet.parseInt(mutationHistory[11]), xCol1 + 8*spacing, yLine2);
        text(PApplet.parseInt(mutationHistory[12]), xCol1 + 10*spacing, yLine2);
        text(PApplet.parseInt(mutationHistory[13]), xCol1 + 12*spacing, yLine2);

        text("Enter point mutations:", xCol1, yLine1 - 2*spacing);
        text("Enter copy numbers:", xCol1, yLine2 - spacing);
        text("EGFR", xCol1, yLine1 - spacing);
        text("KRAS", xCol1 + 2*spacing, yLine1 - spacing);
        text("BRAF", xCol1 + 4*spacing, yLine1 - spacing);
        text("CMYK", xCol1 + 6*spacing, yLine1 - spacing);
        text("AKT1", xCol1 + 8*spacing, yLine1 - spacing);
        text("APC", xCol1 + 10*spacing , yLine1 - spacing);
        text("CTNNB1", xCol1 + 12*spacing, yLine1 - spacing);

        int yLine3 = 113;
        int yLine4 = 113;
        int xCol2 = 855;
        int xCol3 = 1090;
        int labelSpaceLeft = 185;
        int labelSpaceRight = 185;
        spacing = 14;

        text(sheddingHeight, xCol2, yLine3); //height above which death always certain
        text(endsim, xCol2, yLine3 + 2*spacing); //0- for time to monoclonality finish, 1- for timed finish
        text(wntLevel, xCol2, yLine3 + 4*spacing); //Percentage Level of WNT
        text(egfLevel, xCol2, yLine3 + 6*spacing); //Percentage Level of EGF
        text(cryptWidth, xCol2, yLine3 + 8*spacing); //cells wide
        text(cryptLength, xCol2, yLine3 + 10*spacing); //cells high
        text(sccNo, xCol2, yLine3 + 12*spacing);
        text(telomereLength, xCol2, yLine3 + 14*spacing); //initial size of telomeres, decreases by 1 every division, cell arrrests after depletio
        text(copyIncreaseImpact, xCol2, yLine3 + 16*spacing);
        text(copyDecreaseImpact, xCol2, yLine3 + 18*spacing);
        text(copyIncreaseImpactAPC, xCol2, yLine3 + 20*spacing);
        text(copyDecreaseImpactAPC, xCol2, yLine3 + 22*spacing);
        text(minCellCycle, xCol2, yLine3 + 24*spacing);
        text(maxCellCycle, xCol2, yLine3 + 26*spacing);
        text(thresholdCTNNB1, xCol2, yLine3 + 28*spacing);
        text(thresholdCMYC, xCol2, yLine3 + 30*spacing);
        text(timeend, xCol3, yLine4 + 0*spacing);
        text(TACMoveLU, xCol3, yLine4 + 2*spacing);
        text(TACMoveRU, xCol3, yLine4 + 4*spacing);
        text(TACMoveL, xCol3, yLine4 + 6*spacing);
        text(TACMoveR, xCol3, yLine4 + 8*spacing);
        text(TACMoveLD, xCol3, yLine4 + 10*spacing);
        text(TACMoveRD, xCol3, yLine4 + 12*spacing);
        text(TACMoveU, xCol3, yLine4 + 14*spacing);
        text(SBMoveLU, xCol3, yLine4 + 17*spacing);
        text(SBMoveRU, xCol3, yLine4 + 19*spacing);
        text(SBMoveL, xCol3, yLine4 + 21*spacing);
        text(SBMoveR, xCol3, yLine4 + 23*spacing);
        text(SBMoveU, xCol3, yLine4 + 25*spacing);
        text(SCCMoveU, xCol3, yLine4 + 28*spacing);
        text(SCCMoveL, xCol3, yLine4 + 30*spacing);

        text("Shedding height (y):", xCol2 - labelSpaceLeft, yLine3);
        text("Sim end type (0/1):", xCol2 - labelSpaceLeft, yLine3 + 2*spacing);
        text("Wnt level (%):", xCol2 - labelSpaceLeft, yLine3 + 4*spacing);
        text("EGF level (%):", xCol2 - labelSpaceLeft, yLine3 + 6*spacing);
        text("Crypt width (#cells):", xCol2 - labelSpaceLeft, yLine3 + 8*spacing);
        text("Crypt length (#cells):", xCol2 - labelSpaceLeft, yLine3 + 10*spacing);
        text("Stem cells (#cells):", xCol2 - labelSpaceLeft, yLine3 + 12*spacing);
        text("Telomeres (#):", xCol2 - labelSpaceLeft, yLine3 + 14*spacing);
        text("Act. ratio for copy inc. (%):", xCol2 - labelSpaceLeft, yLine3 + 16*spacing);
        text("Act. ratio for copy dec. (%):", xCol2 - labelSpaceLeft, yLine3 + 18*spacing);
        text("Act. ratio for copy inc. APC (%):", xCol2 - labelSpaceLeft, yLine3 + 20*spacing);
        text("Act. ratio for copy dec. APC (%):", xCol2 - labelSpaceLeft, yLine3 + 22*spacing);
        text("Min cell cycle (multiple of 6hrs):", xCol2 - labelSpaceLeft, yLine3 + 24*spacing);
        text("Max cell cycle (multiple of 6hrs):", xCol2 - labelSpaceLeft, yLine3 + 26*spacing);
        text("CTNNB1 threshold lvl (integer):", xCol2 - labelSpaceLeft, yLine3 + 28*spacing);
        text("cMyc threshold lvl (integer):", xCol2 - labelSpaceLeft, yLine3 + 30*spacing);
        text("Sim end time (#days):", xCol3 - labelSpaceRight, yLine4 + 0*spacing);
        text("TAC comp. div. up left (%):", xCol3 - labelSpaceRight, yLine4 + 2*spacing);
        text("TAC comp. div. up right (%):", xCol3 - labelSpaceRight, yLine4 + 4*spacing);
        text("TAC comp. div. left (%):", xCol3 - labelSpaceRight, yLine4 + 6*spacing);
        text("TAC comp. div. right (%):", xCol3 - labelSpaceRight, yLine4 + 8*spacing);
        text("TAC comp. div. down left (%):", xCol3 - labelSpaceRight, yLine4 + 10*spacing);
        text("TAC comp. div. down right (%):", xCol3 - labelSpaceRight, yLine4 + 12*spacing);
        text("TAC comp. div. up (%): \n (TAC remainder goes down)", xCol3 - labelSpaceRight, yLine4 + 14*spacing);
        text("SupB div. up left (%):", xCol3 - labelSpaceRight, yLine4 + 17*spacing);
        text("SupB div. up right (%):", xCol3 - labelSpaceRight, yLine4 + 19*spacing);
        text("SupB div. left (%):", xCol3 - labelSpaceRight, yLine4 + 21*spacing);
        text("SupB div. right (%):", xCol3 - labelSpaceRight, yLine4 + 23*spacing);
        text("SupB div. up (%): \n (SubB remainder goes down)", xCol3 - labelSpaceRight, yLine4 + 25*spacing);
        text("Basal div. up (%):", xCol3 - labelSpaceRight, yLine4 + 28*spacing);
        text("Basal div. left (%): \n (Basal remainder goes right)", xCol3 - labelSpaceRight, yLine4 + 30*spacing);

        saveFrame("images/ts-#########.png");
    }



    //timer code
    //long totalTime  =0;
    //int w = 0;
    //long start = System.nanoTime();
    //long end = System.nanoTime();
    //totalTime = totalTime + end - start;
    //if (w%200 == 0) {
    //println(String.format("%.2f ms",((totalTime)/(1000000.0))/200));
    //totalTime = 0;
    //}
    //w = w+1;


// cell system with ArrayList
//states: 0-just born 1-normal 2-going to die

    class Cell {
        //All cells have a location in space

        int x;
        int y;
        int tree; //philogenetic tree
        int state; // tag for whatever we want to display. In this version color/10 is number of divisions
        float age=0;
        int div=0;
        int newDiv=0;

        int telomere=Crypt.telomereLength;
        int cellcycle=0;

        //This is a provitional set of internal properties that will be replaced by a descicion network
        int tumor_suppression=2;
        int oncogenes=2;

        //This controls the rate of mutation. (setting it to 1000 would mean the rate is 1 in 1000)
        int mutationTS=1; //tumor supression mutation
        int mutationOG=1; // oncogene mutation
        int missegregation=0; //rate of chromosome missegregation
        float lifespan=10000; //lifespan of each cell

        //There are the initial colors
        int filla=255;
        int fillb=255;
        int fillc=255;
        int filltext=0;

        int growth;
        int apoptosis;

        //Initializing diploid genome.
//Each space represents one chromosome. We start with 2 chromosomes of two genes, with no mutations.
//intial genes are 0: EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
        int [] mutations =  {0,0,0,0,0,0,0};
        int [] copyNumber = {2,2,2,2,2,2,2};


        //We read each cell in the list every time step and decide its fate
        Cell(int x0, int y0, int tree0, int state0, float age0, int div0, int tel0, int al0, int al1, int al2, int al3, int al4, int al5, int al6, int cn0, int cn1, int cn2, int cn3, int cn4, int cn5, int cn6) {
            //We read the cell's position
            x= x0;
            y= y0;
            tree= tree0;
            state=state0;
            age=age0;
            div=div0;
            newDiv=0;
            telomere=tel0;
            mutations[0] = al0;
            mutations[1] = al1;
            mutations[2] = al2;
            mutations[3] = al3;
            mutations[4] = al4;
            mutations[5] = al5;
            mutations[6] = al6;
            copyNumber[0] = cn0;
            copyNumber[1] = cn1;
            copyNumber[2] = cn2;
            copyNumber[3] = cn3;
            copyNumber[4] = cn4;
            copyNumber[5] = cn5;
            copyNumber[6] = cn6;
        }


        //This method ages the cells.
        public void aging() {
            lifespan=lifespan-0.25f;
            age=age+0.25f;
            cellcycle=cellcycle+6; //progress 6 hours per time (1/4 of a day)
            newDiv=0;
        }


        public void display() {
            stroke(0);
            colorMode(HSB);
            //If cells are new

            //Ephitelium - case 0
            if(Crypt.vis==0 && y<Crypt.egfTotal){
                filla=tree*30;
                fillb=50;
                fillc=150;
                filltext=tree;}

            //Stem Cell compartment - case 0
            else if(Crypt.vis==0 && y>=Crypt.wntTotal){
                filla=tree*30;
                fillb=120;
                fillc=250;
                filltext=tree;}

            //Transit Amplifying Cells - case 0
            else if (Crypt.vis==0) {
                filla=tree*30;
                fillb=100;
                fillc=210;
                filltext=tree;}

            // All cells - case 1
            else if (Crypt.vis==1) //if state (the timesteps int the program)
            {
                filla=(int)(state*10);//changes color on number of divisions
                fillb=287;
                fillc=288;
                filltext=state;}

            // All cells - case 2
            else if (Crypt.vis==2) //if state (the timesteps int the program)
            {
                filla=(int)(age*10);//changes color on age
                fillb=287;
                fillc=288;
                filltext=PApplet.parseInt(age);}

            //Ephitelium - case 3
            else if(Crypt.vis==3 && newDiv==1) {
                filla=360;//changes color being recently dvidied
                fillb=287;
                fillc=288;
                filltext=telomere;
            }

            else if(Crypt.vis==3 && y<Crypt.egfTotal){
                filla=5;
                fillb=50;
                fillc=262;
                filltext=telomere;}

            //Stem Cell compartment - case 3
            else if(Crypt.vis==3 && y>=Crypt.wntTotal){
                filla=90;
                fillb=50;
                fillc=262;
                filltext=telomere;}

            //Transit Amplifying Cells - case 3
            else if (Crypt.vis==3) {
                filla=40;
                fillb=50;
                fillc=262;
                filltext=telomere;}

            // All cells - case 4
            else if (Crypt.vis==4) //if state (the timesteps int the program)
            {
                filla=(int)(totalMutations()*10);//changes color on mutation count
                fillb=287;
                fillc=288;
                filltext=totalMutations();}

            // All cells - case 5
            else if (Crypt.vis==5) //if state (the timesteps int the program)
            {
                filla=(int)(cellType()*30);//changes color on mutation count
                fillb=287;
                fillc=288;
                filltext=cellType();}

            //make cells

            fill(filla,fillb,fillc);
            ellipse(x,y,15,15);
            textSize(9);                 // STEP 4 Specify font to be used
            fill(0);                        // STEP 5 Specify font color
            text(filltext,x-5,y+4);
        }

        //These conditions test whether the cell fullfillts the criteria and returns either true or false
        public boolean isDead() { //This tests for cell death
            // Propability of death in any layer
            if (y< Crypt.sheddingHeight){ return true;}
            else if (random(100)<=100*deathProb(WntLevel(y), EGFLevel(y))) {
                return true;}
            else {return false;}
        }

        //gene signal pathways
        //for oncogenes
        public float oncogene(float in, int geneNo) {
            float x = 1;
            if (copyNumber[geneNo] == 0) {x = 0;}
            else if (copyNumber[geneNo] > 2) {x = 1.1f;}
            else if (copyNumber[geneNo] < 2) {x = 0.75f;}
            if (mutations[geneNo] ==0){return x*in;}
            else {return 120;}
        }

        public float CTNNB1(float in, int geneNo, float APC_out) {
            float x = 1;
            if (copyNumber[geneNo] == 0) {x = 0;}
            else if (copyNumber[geneNo] > 2) {x = 1.1f;}
            else if (copyNumber[geneNo] < 2) {x = 0.75f;}
            if (mutations[geneNo] ==0){
                if (in>0){return x*in - x*in*APC_out/100;}
                else {return 0;}
            }
            else {return 110;}
        }

        public float APC(float in, int geneNo) {
            float x = 1;
            if (copyNumber[geneNo] == 0) {x = 0;}
            else if (copyNumber[geneNo] > 2) {x = 1.1f;}
            else if (copyNumber[geneNo] < 2) {x = 0.9f;}
            if (mutations[geneNo] ==0){return x*(100-in);}
            else {return 0;}
        }

        public float proliferationProb(int inEGF, int inWnt) {
            //intial genes are 0: EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
            // pathway: EGF gradient->EGFR->KRAS->BRAF->CMYK-> output
            float EGFR = oncogene(inEGF,0); //EGFR
            float KRAS = oncogene(EGFR,1); //KRAS
            float BRAF = oncogene(KRAS,2); //BRAF
            float CMYK1 = oncogene(BRAF, 3); //CMYK
            float AKT1 = oncogene(KRAS,4); //AKT1
            float APC = APC(inWnt,5); //APC
            float CTNNB1 = CTNNB1(AKT1,6,APC); //CTNNB1
            int x;
            if( mutations[5] + mutations[4] + mutations[6] != 0 || copyNumber[5]<2)
            {x = 1;}
            else
            {x = 0;}
            return min(100,(CMYK1*100/125 + max(0,CTNNB1-80)*x));
        }

        public float deathProb(int inWnt, int inEGF) {
            //intial genes are 0: EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
            // pathway1:           EGF gradient->EGFR->KRAS->BRAF
            // pathway2:             Wnt gradient->APC
            // pathway3: EGF gradient->EGFR->KRAS->AKT1
            // pathway4:                           APC&AKT1->CTNNB1
            // pathway5:                                     CTNNB1 & BRAF ->output

            float EGFR = oncogene(inEGF,0); //EGFR
            float KRAS = oncogene(EGFR,1); //KRAS
            float BRAF = oncogene(KRAS,2); //BRAF

            float AKT1 = oncogene(KRAS,4); //AKT1
            float APC = APC(inWnt,5); //APC
            float CTNNB1 = CTNNB1(AKT1,6,APC); //CTNNB1

            if(CTNNB1 >0){return 0;}
            else {return (100 - BRAF)*0.01f;}
        }


        //WNT and EGF gradient calculations given y position
        public int WntLevel(int y){
            int gradient_difference=(Crypt.SCClimit - Crypt.wntTotal);
            int wnt_grad=max(0,100+(int)((y-Crypt.wntTotal-gradient_difference)*(100.0f/(float)gradient_difference)));//should be 100% at the bottom, and less as it moves up until 0%
            return wnt_grad;
        }

        public int EGFLevel(int y){
            int gradient_difference=(Crypt.SCClimit-Crypt.egfTotal);
            int egf_grad=max(0,100+(int)((y-Crypt.egfTotal-gradient_difference)*(100.0f/(float)gradient_difference)));//should be 100% at the bottom, and less as it moves up until 0%
            return egf_grad;
        }

        public int refractoryHours(int inWnt, int inEGF){
            //intial genes are 0: EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
            // pathway1:             Wnt gradient->APC
            // pathway2: EGF gradient->EGFR->KRAS->AKT1
            // pathway3:                           APC&AKT1->CTNNB1

            float EGFR = oncogene(inEGF,0); //EGFR
            float KRAS = oncogene(EGFR,1); //KRAS
            float AKT1 = oncogene(KRAS,4); //AKT1
            float APC = APC(inWnt,5); //APC
            float CTNNB1 = CTNNB1(AKT1,6,APC); //CTNNB1

            if(CTNNB1 > 40){return Crypt.maxCellCycle;}
            else {return Crypt.minCellCycle;} //output hours
        }

        public int cellFate(int inWnt, int inEGF){
            //intial genes are 0: EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
            // pathway1:             Wnt gradient->APC
            // pathway2: EGF gradient->EGFR->KRAS->AKT1
            // pathway3:                           APC&AKT1->CTNNB1
            // pathway4: EGF gradient->EGFR->KRAS->BRAF->CMYK-> output

            float EGFR = oncogene(inEGF,0); //EGFR
            float KRAS = oncogene(EGFR,1); //KRAS
            float AKT1 = oncogene(KRAS,4); //AKT1
            float APC = APC(inWnt,5); //APC
            float CTNNB1 = CTNNB1(AKT1,6,APC); //CTNNB1

            float BRAF = oncogene(KRAS,2); //BRAF
            float CMYK1 = oncogene(BRAF, 3); //CMYK

            int x;
            int y;
            if (CTNNB1 > 25) {x = 1;}
            else {x = 0;}
            if (CMYK1 > 0) {y = 1;}
            else {y = 0;}

            return x + y; //output cell fate; 0 = differentiated, 1 = TAC, 2 = SC
        }

        public int sideDisplacement(){
            //if APC or CTNNB1 are mutated then 24% otherwise normal 12%
            if(mutations[5] + mutations[6] != 0 || copyNumber[5] == 0)
            {return 2;}
            else
            {return 1;}
        }

        //New method for division
        public boolean divide() {

            if (cellcycle>=refractoryHours(WntLevel(y), EGFLevel(y)) && telomere>0 && y<=Crypt.SCClimit)//Check for their cell cycle and Telomeres
            {
                // Probability of division in the SCC
                if (cellType() == 2 && random(100)<proliferationProb(EGFLevel(y), WntLevel(y))) {
                    state=state+1; //keeps track of cell divisions
                    newDiv=1; //counter for recently divided cells
                    cellcycle = 0; //reset counter for next division
                    return true;}

                //Probability of division in TAC, directly proportional to EGF
                else if (cellType() != 2 && random(100)<proliferationProb(EGFLevel(y), WntLevel(y))) {
                    state=state+1; //keeps track of cell divisions
                    newDiv=1; //counter for recently divided cells
                    telomere=telomere-1;//decrease of telomeres
                    cellcycle = 0; //reset count for next division
                    return true;}
                else {return false;}

            }

            else {return false;}

        }



//boolean WNT_APC () {
// //int wnt = y*100/Crypt.wntLevel;
//  //int egf = y*100/Crypt.egfLevel;
//  if (random(Crypt.wntLevel)>(Crypt.SCClimit)) {
//return true;
//    } else {
//      return false;
//    }
//}

//boolean GenomicStress() {
//int stress = genome[1]+genome[2]+genome[3]+genome[4]+genome[5]+genome[6]+genome[7]+genome[8]+genome[9]+genome[10]+genome[11]+genome[12]+genome[13]+genome[14]+genome[15]+genome[16]+genome[17]+genome[18]+genome[19]+genome[20]+genome[21]+genome[22]+genome[23]+genome[24];
////minimum number of chromosomes needed
//if (stress<(2*15)) {
//return true;
//    } else {
//      return false;
//    }
//}

////If tumor suppression is still on, cells should die when they reach the carying capacity
//boolean ContactInhibition() {
//int growthlimit= 350;
//if (y<growthlimit && tumor_suppression>0){
//  return true;
//    } else {
//      return false;
//    }
//}
////doesn't let old cells live
//boolean GrowthInhibition() {
//int agelimit=100;
//if (age>agelimit){
//  return true;
//    } else {
//      return false;
//    }
//}

        //total number of mutation
        public int totalMutations(){
            int total = 0;
            for (int i = 0; i< mutations.length; i++){
                total = total + mutations[i];
            }
            for (int i = 0; i< copyNumber.length; i++){
                total = total + abs(copyNumber[i] -2);
            }
            return total;
        }



        //this method passes on the coordinates of each cell to crypt
        public int divisionX() {
            int x2=x;
            return x2;
        }

        public int divisionY() {
            int y2=y;
            return y2;
        }

        public int divisionTree() {
            int tree2=tree;
            return tree2;
        }

        public int divisionState() {
            int state2=state;
            return state2;
        }

        public float divisionAge() {
            float age2=age;
            return age2;
        }

        public int telomere() {
            int telomere2=telomere;
            return telomere2;
        }

        public int mutationsNumbers(int n) {
            int mut = mutations[n];
            return mut;
        }

        public int copyNumbers(int n) {
            int cN = copyNumber[n];
            return cN;
        }

        public int cellType() {
            int cT = cellFate(WntLevel(y), EGFLevel(y));
            return cT;
        }

        public int sideDiv() {
            int div = sideDisplacement();
            return div;
        }


        //this method handles the physics of pushing cells on top, when a cell is dividing
        public void pushedY(){
            y=y-Crypt.r;
        }

        //this method handles the physics of pulling cells on top to fill in the gap when a cell has died
        public void pulledY(){
            //if(y<(Crypt.SCClimit-r)){y=y+Crypt.r;}
            y=y+Crypt.r;
        }

        //This method is for death in the SSC
        public void pulledYSSC(int dispX){

            if (y<=Crypt.SCClimit-2*r)
            {y=y+Crypt.r;}
            else if (y==Crypt.SCClimit-r)
            {y=Crypt.SCClimit;
                x=dispX;}

        }



        //  boolean finished() {
        //  if(cells.size() == 0){
        //  return true;
        //} else{
        //return false;
        //}
        //  }

        //mutation method1
        public void mutateNormal() {
            //mutate a random gene //turn off mutations/genes here
            if (Crypt.mutationsOn ==true) {
                if (Crypt.chooseMutationOn == false){
                    //int allele = int(random(4));
                    //tree = tree + 10+allele;
                    ////gene 1 - PIK3CA
                    //if (allele == 0){
                    //mutations[0] = mutations[0]+1;
                    //}
                    //else if (allele == 1){
                    //mutations[1] = mutations[1] +1;
                    //}
                    ////gene 2 - TGFBR2
                    //else if (allele == 2){
                    //mutations[2] = mutations[2] +1;
                    //}
                    //else if (allele == 3){
                    //mutations[3] = mutations[3] +1;
                    //}
                }
                else {
                    //intial genes are 0: EGFR, 1: KRAS, 2: BRAF, 3: CMYK, 4: AKT1, 5: APC, 6: CTNNB1
                    // enter

                    mutations[0] = 0;
                    mutations[1] = 0;
                    mutations[2] = 0;
                    mutations[3] = 0;
                    mutations[4] = 0;
                    mutations[5] = 0;
                    mutations[6] = 0;
                    copyNumber[0] = 2;
                    copyNumber[1] = 2;
                    copyNumber[2] = 2;
                    copyNumber[3] = 2;
                    copyNumber[4] = 2;
                    copyNumber[5] = 2;
                    copyNumber[6] = 2;

                    if (Crypt.mutantValue <=6) {
                        mutations[Crypt.mutantValue] =  1;
                        tree = tree + 5; }
                    else if (Crypt.mutantValue <= 13){
                        copyNumber[Crypt.mutantValue - 7] = 3;
                        tree = tree + 5; }
                    else if (Crypt.mutantValue <= 20){
                        copyNumber[Crypt.mutantValue - 14] = 1;
                        tree = tree + 5; }
                    else if (Crypt.mutantValue <= 27){
                        copyNumber[Crypt.mutantValue - 21] = 0;
                        tree = tree + 5; }

                }


            }
        }

        //mutation method2. Takes inputs from user and changes the selected cells genome and tree number
        public void mutateGUI(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11, int a12, int a13, int a14) {
            mutations[0] = 0;
            mutations[1] = 0;
            mutations[2] = 0;
            mutations[3] = 0;
            mutations[4] = 0;
            mutations[5] = 0;
            mutations[6] = 0;
            copyNumber[0] = 2;
            copyNumber[1] = 2;
            copyNumber[2] = 2;
            copyNumber[3] = 2;
            copyNumber[4] = 2;
            copyNumber[5] = 2;
            copyNumber[6] = 2;

            mutations[0] = a1;
            mutations[1] = a2;
            mutations[2] = a3;
            mutations[3] = a4;
            mutations[4] = a5;
            mutations[5] = a6;
            mutations[6] = a7;
            copyNumber[0] = a8;
            copyNumber[1] = a9;
            copyNumber[2] = a10;
            copyNumber[3] = a11;
            copyNumber[4] = a12;
            copyNumber[5] = a13;
            copyNumber[6] = a14;

            tree = Crypt.mutant;
            Crypt.mutant = Crypt.mutant + 1;

        }
    }



    class Logger
    {
        String m_fileName;

        Logger(String fileName)
        {
            m_fileName = fileName;
        }

        public void log(String line)
        {
            PrintWriter pw = null;
            try
            {
                pw = GetWriter();
                pw.println(line);
                println(line);
            }
            catch (IOException e)
            {
                e.printStackTrace(); // exception handling...
                println("ouch 1");
            }
            finally
            {
                if (pw != null)
                {
                    pw.close();
                }
            }
        }

        public void log(String[] lines)
        {
            PrintWriter pw = null;
            try
            {
                pw = GetWriter();
                for (int i = 0; i < lines.length; i++)
                {
                    pw.println(lines[i]);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace(); // exception handling...
                println("ouch 2");
            }
            finally
            {
                if (pw != null)
                {
                    pw.close();
                }
            }
        }

        public void log(String errorMessage, StackTraceElement[] ste)
        {
            PrintWriter pw = null;
            try
            {
                pw = GetWriter();
                pw.println(errorMessage);
                for (int i = 0; i < ste.length; i++)
                {
                    pw.println("\tat " + ste[i].getClassName() + "." + ste[i].getMethodName() +
                            "(" + ste[i].getFileName() + ":" + ste[i].getLineNumber() + ")"
                    );
                }
            }
            catch (IOException e)
            {
                e.printStackTrace(); // exception handling...
                println("ouch 3");
            }
            finally
            {
                if (pw != null)
                {
                    pw.close();
                }
            }
        }

        private PrintWriter GetWriter() throws IOException
        {
            // FileWriter with append, BufferedWriter for performance
            // (although we close each time, not so efficient...), PrintWriter for convenience
            return new PrintWriter(new BufferedWriter(new FileWriter(m_fileName, true)));
        }
    }
    public void settings() {  size(1200,780); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "Crypt" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }
}

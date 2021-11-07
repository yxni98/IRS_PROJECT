import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry; 
import java.io.FileWriter;

// javac PoiFHSM.java

// java PoiFHSM -d 20 -	 -alpha 0.5 -alpha_w 0.01 -alpha_v 0.01 -beta_v 0.01 -gamma 0.01 -fnTrainData ML100K-copy1-train -fnTestData ML100K-copy1-test -fnValidData ML100K-copy1-valid -n 943 -m 1682 -num_iterations 100 -topK 8 MRR MAP ARP AUC


public class PoiFHSM
{
    // === Configurations       
    // the number of latent dimensions
    public static int d = 20;
    // $\alpha$
    public static float alpha = 0.5f;
    // tradeoff $\alpha_w$
    public static float alpha_w = 0.01f;    
    // tradeoff $\alpha_v$
    public static float alpha_v = 0.01f;
    // tradeoff $\beta_v$
    public static float beta_v = 0.01f;
    // learning rate $\gamma$
    public static float gamma = 0.001f;
    // tradeoff $\lambda$
    public static float lambda = 0.4f;  // when lambda=0, it reduces to P-FISM
        
    // === Data
    
    public static String fnTrainData = "";
    public static String fnTestData = "";
    public static String fnValidData = "";
    
    // 
    public static int n = 0; // number of users
    public static int m = 0; // number of items  
    public static int num_train = 0; // number of the total (user, item) pairs in training data
    public static int num_iterations = 500; // scan number over the whole data 
    
    // === Evaluation
    // 
    public static int topK = 8; // top k in evaluation
    
    public static int rho = 3; // sampling ratio
    //  
     // 
    public static boolean flagMRR = false;
    public static boolean flagMAP = false;
    public static boolean flagARP = false;
    public static boolean flagAUC = false;
        
    // === training data
    public static HashMap<Integer, HashSet<Integer>> TrainData = new HashMap<Integer, HashSet<Integer>>();
        // --- user -> item set
    public static HashMap<Integer, HashSet<Integer>> TrainDataItem2User = new HashMap<Integer, HashSet<Integer>>();
        // --- item -> user set
    
    // === training data used for uniformly random sampling
    public static int[] indexUserTrain; // start from index "0", used to uniformly sample (u, i) pair
    public static int[] indexItemTrain; // start from index "0", used to uniformly sample (u, i) pair
    
    // === validation data
    public static HashMap<Integer, HashSet<Integer>> ValidData = new HashMap<Integer, HashSet<Integer>>();
        // --- user -> item set    
    
    // === test data
    public static HashMap<Integer, HashSet<Integer>> TestData = new HashMap<Integer, HashSet<Integer>>();
        // --- user -> item set
        
    //item-item similarity
    public static HashMap<Integer, HashMap<Integer, Float>>  item2itemSimilarity 
        = new HashMap<Integer, HashMap<Integer,Float>>();
    
    // === whole data (users)
    public static HashSet<Integer> UserSetWhole = new HashSet<Integer>();
    
    // === whole data (items)
    public static HashSet<Integer> ItemSetWhole = new HashSet<Integer>();

    // === some statistics, start from index "1"
    public static int[] itemRatingNumTrain;
    public static int[] userRatingNumTrain; 
    
    // === model parameters to learn, start from index "1"
    public static float[][] W;
    public static float[][] U;
    public static float[][] V;
    public static float[] biasV;  // bias of item
    public static float[] biasU;  // bias of user
       
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String[] args) throws Exception
    {   
    	// =========================================================
		// === Read the configurations
        for (int k=0; k < args.length; k++)
        {
    		if (args[k].equals("-d")) d = Integer.parseInt(args[++k]);
			else if (args[k].equals("-alpha")) alpha = Float.parseFloat(args[++k]);
    		else if (args[k].equals("-alpha_w")) alpha_w = Float.parseFloat(args[++k]);
    		else if (args[k].equals("-alpha_v")) alpha_v = Float.parseFloat(args[++k]);        		
    		else if (args[k].equals("-beta_v")) beta_v = Float.parseFloat(args[++k]);    		
			else if (args[k].equals("-lambda")) lambda = Float.parseFloat(args[++k]);
			else if (args[k].equals("-gamma")) gamma = Float.parseFloat(args[++k]);
    		else if (args[k].equals("-fnTrainData")) fnTrainData = args[++k];    		
    		else if (args[k].equals("-fnTestData")) fnTestData = args[++k];
    		else if (args[k].equals("-fnValidData")) fnValidData = args[++k];
    		else if (args[k].equals("-n")) n = Integer.parseInt(args[++k]);
    		else if (args[k].equals("-m")) m = Integer.parseInt(args[++k]);
    		else if (args[k].equals("-num_iterations")) num_iterations = Integer.parseInt(args[++k]);
    		else if (args[k].equals("-topK")) topK = Integer.parseInt(args[++k]);  
			else if (args[k].equals("MRR")) flagMRR = true;
    		else if (args[k].equals("MAP")) flagMAP = true;
    		else if (args[k].equals("ARP")) flagARP = true;
    		else if (args[k].equals("AUC")) flagAUC = true;  
        }
        
		// =========================================================
    	// === Print the configurations
		System.out.println(Arrays.toString(args));
		
    	System.out.println("d: " + Integer.toString(d));
		System.out.println("alpha: " + Float.toString(alpha));
    	System.out.println("alpha_w: " + Float.toString(alpha_w));
    	System.out.println("alpha_v: " + Float.toString(alpha_v));
    	System.out.println("beta_v: " + Float.toString(beta_v));    	
		System.out.println("lambda: " + Float.toString(lambda));
		System.out.println("gamma: " + Float.toString(gamma));
    	System.out.println("fnTrainData: " + fnTrainData);
    	System.out.println("fnTestData: " + fnTestData);
    	System.out.println("fnValidData: " + fnValidData);
    	    	
    	System.out.println("n: " + Integer.toString(n));
    	System.out.println("m: " + Integer.toString(m));
                
        System.out.println("num_iterations: " + Integer.toString(num_iterations));
        
        System.out.println("topK: " + Integer.toString(topK));
        System.out.println("flagMRR: " + Boolean.toString(flagMRR));
        System.out.println("flagMAP: " + Boolean.toString(flagMAP));
        System.out.println("flagARP: " + Boolean.toString(flagARP));
        System.out.println("flagAUC: " + Boolean.toString(flagAUC));
        // =========================================================
        
        // --- some statistics 
        itemRatingNumTrain = new int[m+1]; // start from index "1"
        userRatingNumTrain = new int[n+1]; // start from index "1"
        
        // =========================================================
        // === Locate memory for the data structure of the model parameters
        W = new float[m+1][d];
        V = new float[m+1][d];
        U = new float[n+1][d];
        biasV = new float[m+1];  // bias of item        
        biasU = new float[n+1];  // bias of user
        // =========================================================
        
        // =========================================================
        // === Step 1: Read data
        long TIME_START_READ_DATA = System.currentTimeMillis();
        readDataTrainTest();
        long TIME_FINISH_READ_DATA = System.currentTimeMillis();
        System.out.println("Elapsed Time (read data):" + 
                    Float.toString((TIME_FINISH_READ_DATA-TIME_START_READ_DATA)/1000F)
                    + "s");     
        // =========================================================
        System.out.println( "num_train: " + Integer.toString(num_train));
        
        // =========================================================
        // --- construct indexUserTrain and indexItemTrain
        indexUserTrain = new int[num_train];
        indexItemTrain = new int[num_train];
                
        int idx = 0;
        for(int u=1; u<=n; u++)
        {
            // --- check whether the user $u$ is in the training data
            if (!TrainData.containsKey(u))
                continue;
            
            // --- get a copy of the data in indexUserTrain and indexItemTrain
            HashSet<Integer> ItemSet = new HashSet<Integer>();
            if (TrainData.containsKey(u))
            {
                ItemSet = TrainData.get(u);
            }
            for(int i : ItemSet)
            {
                indexUserTrain[idx] = u;
                indexItemTrain[idx] = i;
                idx += 1;
            }
        }
        // =========================================================
        
        // =========================================================
        // === Step 2: Initialization of U, W, V
        long TIME_START_INITIALIZATION = System.currentTimeMillis();
        initialize();
        long TIME_FINISH_INITIALIZATION = System.currentTimeMillis();
        System.out.println("Elapsed Time (initialization):" + 
                    Float.toString((TIME_FINISH_INITIALIZATION-TIME_START_INITIALIZATION)/1000F)
                    + "s");
        // =========================================================
         
        // =========================================================
         //compute item2item similarity
        //step 3  compute item2item similarity
        if(lambda>0)
        {
            long TIME_START_getItem2ItemSimilarity = System.currentTimeMillis();
            getItem2ItemSimilarity();
            long TIME_FINISH_getItem2ItemSimilarity = System.currentTimeMillis();
            System.out.println("Elapsed Time (getItem2ItemSimilarity):" + 
                        Float.toString((TIME_FINISH_getItem2ItemSimilarity-TIME_START_getItem2ItemSimilarity)/1000F)
                        + "s");
        }
        // =========================================================
        // === Step 4: Training
        long TIME_START_TRAIN = System.currentTimeMillis();
        train();
        long TIME_FINISH_TRAIN = System.currentTimeMillis();
        System.out.println("Elapsed Time (training):" + 
                    Float.toString((TIME_FINISH_TRAIN-TIME_START_TRAIN)/1000F)
                    + "s");
        // =========================================================
        
        // =========================================================
        // === Step 5: Prediction and Evaluation
        if (fnValidData.length()>0)
        {
            long TIME_START_TEST = System.currentTimeMillis();
            testRanking(ValidData);
            long TIME_FINISH_TEST = System.currentTimeMillis();
            System.out.println("Elapsed Time (validation):" + 
                        Float.toString((TIME_FINISH_TEST-TIME_START_TEST)/1000F)
                        + "s");
        }
        // =========================================================
        if (fnTestData.length()>0)
        {
            long TIME_START_TEST = System.currentTimeMillis();
            testRanking(TestData);
            long TIME_FINISH_TEST = System.currentTimeMillis();
            System.out.println("Elapsed Time (test):" + 
                        Float.toString((TIME_FINISH_TEST-TIME_START_TEST)/1000F)
                        + "s");
        }
        // =========================================================
    }
        
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void readDataTrainTest() throws Exception
    {           
        // =========================================================
        BufferedReader br = new BufferedReader(new FileReader(fnTrainData));
        String line = null;
                
        while ( (line = br.readLine())!=null )
        {
            String[] terms = line.split("\\s+");
            int userID = Integer.parseInt(terms[0]);  
            int itemID = Integer.parseInt(terms[1]);  
            
            // --- add to the whole user set
            UserSetWhole.add(userID);  
            
            // --- add to the whole item set
            ItemSetWhole.add(itemID);       
                                    
            // --- user -> item set
            if(TrainData.containsKey(userID))
            {
                HashSet<Integer> itemSet = TrainData.get(userID);
                itemSet.add(itemID);
                TrainData.put(userID, itemSet);
            }
            else
            {
                HashSet<Integer> itemSet = new HashSet<Integer>();
                itemSet.add(itemID);
                TrainData.put(userID, itemSet);
            }
                        
            // --- item -> user set
            if(TrainDataItem2User.containsKey(itemID))
            {
                HashSet<Integer> userSet = TrainDataItem2User.get(itemID);
                userSet.add(userID);
                TrainDataItem2User.put(itemID, userSet);
            }
            else
            {
                HashSet<Integer> userSet = new HashSet<Integer>();
                userSet.add(userID);
                TrainDataItem2User.put(itemID, userSet);
            }
            
            // --- statistics, used to calculate the performance on different user groups
            itemRatingNumTrain[itemID] += 1;
            userRatingNumTrain[userID] += 1;
            num_train += 1; // the number of total user-item pairs
        } // --- Finish reading the training data
        br.close();

        // =========================================================
        if (fnValidData.length()>0)
        {
            br = new BufferedReader(new FileReader(fnValidData));
            line = null;
            while ((line = br.readLine())!=null)
            {
                String[] terms = line.split("\\s+");
                int userID = Integer.parseInt(terms[0]);
                int itemID = Integer.parseInt(terms[1]);
                
                // --- add to the whole user set
                UserSetWhole.add(userID);  
                
                // --- add to the whole item set                
                ItemSetWhole.add(itemID);
                
                // --- ValidData            
                if(ValidData.containsKey(userID))
                {
                    HashSet<Integer> itemSet = ValidData.get(userID);
                    itemSet.add(itemID);
                    ValidData.put(userID, itemSet);
                }
                else
                {
                    HashSet<Integer> itemSet = new HashSet<Integer>();
                    itemSet.add(itemID);
                    ValidData.put(userID, itemSet);
                }           
            }
            br.close();  //reading valid data      
        }
        // =========================================================
            
        // =========================================================
        if (fnTestData.length()>0)
        {
            br = new BufferedReader(new FileReader(fnTestData));
            line = null;
            while ((line = br.readLine())!=null)
            {
                String[] terms = line.split("\\s+");
                int userID = Integer.parseInt(terms[0]);
                int itemID = Integer.parseInt(terms[1]);
                
                // --- add to the whole user set
                UserSetWhole.add(userID);  
                
                // --- add to the whole item set                
                ItemSetWhole.add(itemID);
                
                // --- test data                
                if(TestData.containsKey(userID))
                {
                    HashSet<Integer> itemSet = TestData.get(userID);
                    itemSet.add(itemID);
                    TestData.put(userID, itemSet);
                }
                else
                {
                    HashSet<Integer> itemSet = new HashSet<Integer>();
                    itemSet.add(itemID);
                    TestData.put(userID, itemSet);
                }
            }
            br.close();  //reading test data
        }
        // =========================================================
        
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void initialize()
    {   
        // ======================================================   
        // --- initialization of U, W and V
        for (int i=1; i<n+1; i++)
        {
            for (int f=0; f<d; f++)
            {
                U[i][f] = (float) ( (Math.random()-0.5)*0.01 );
            }
        }

        for (int i=1; i<m+1; i++)
        {
            for (int f=0; f<d; f++)
            {
                W[i][f] = (float) ( (Math.random()-0.5)*0.01 );
            }
        }
        //
        for (int i=1; i<m+1; i++)
        {
            for (int f=0; f<d; f++)
            {
                V[i][f] = (float) ( (Math.random()-0.5)*0.01 );
            }
        }
        // ======================================================
        
        // ======================================================
        // --- initialization of biasV
        float g_avg = 0;
        //int maxItemRatingNumTrain = 0;
        for (int i=1; i<m+1; i++)
        {
            g_avg += itemRatingNumTrain[i];
        }
        g_avg = g_avg/n/m;
        System.out.println( "The global average rating:" + Float.toString(g_avg) );
        
        // --- biasV[i] represents the popularity of the item i, which is initialized to [0,1]
        for (int i=1; i<m+1; i++)
        {
             biasV[i]= (float) itemRatingNumTrain[i] / n - g_avg;
        }
        for (int i=1; i<n+1; i++)
        {
             biasU[i]= (float) userRatingNumTrain[i] / m - g_avg;
        }
        // $ \mu = \sum_{u,i} y_{ui} /n/m $ 
        // $ b_i = \sum_{u=1}^n (y_{ui} - \mu) / n $
        // ======================================================   
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void getItem2ItemSimilarity()
    {
        for(int j=1; j<=m; j++)
        {
            // --- users that prefer item j  !!! make numUser preferred item j not null!!!
            if( !TrainDataItem2User.containsKey(j) )
                continue;
            HashSet<Integer> users_j = TrainDataItem2User.get(j);

            HashMap<Integer, Float> itemSimilarityPairs = new HashMap<Integer, Float>();            
            for(int i=1; i<=m; i++)
            {   
                // need to compute similarity of i itself for W[i]
                // the item is not preferred by any user
                if( !TrainDataItem2User.containsKey(i) )
                    continue;

                // --- users that prefer item i     
                HashSet<Integer> users_i = TrainDataItem2User.get(i); 
                // --- intersection
                int users_ij_num = 0;
                for(int k : users_j)
                {
                    if(users_i.contains(k))
                        users_ij_num++;
                }

                // --- 
                if( users_ij_num > 0 )
                {
                    int users_i_num = users_i.size();
                    int users_j_num = users_j.size();
                    float similarity = (float) (users_ij_num*1.0/Math.sqrt(users_i_num*users_j_num));
                    itemSimilarityPairs.put(i, similarity);                          
                } 
            }
            item2itemSimilarity.put(j, itemSimilarityPairs);            
        }
    }
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void train() throws FileNotFoundException
    {       
    	ArrayList<String> UnobservedList = new ArrayList<String>(); 
    	for(int user : UserSetWhole) 
    	{
    		for(int item : ItemSetWhole)
    		{
    			if(TrainDataItem2User.containsKey(item)&&TrainDataItem2User.get(item).contains(user)) 
    			{
    				continue;
    			}
    			else
    			{
    				UnobservedList.add(Integer.toString(user)+"-"+Integer.toString(item)+"-0");
    			}
        	}
    	}
    	
    	ArrayList<String> ObservedList = new ArrayList<String>(); 
    	for(int item : TrainDataItem2User.keySet())
    	{
    		for(int user : TrainDataItem2User.get(item))
    		{
    			ObservedList.add(Integer.toString(user)+"-"+Integer.toString(item)+"-1");
    		}
    	}
    	// get A (A.size() = rho * num_train)
    	// change the prediction rule
    	// change the update parameters, keep all the gradients(V W, etc)
    	
    	ArrayList<String> UnionList = new ArrayList<String>(); 
    	
        for (int iter = 0; iter < num_iterations; iter++)
        {          
        	Collections.shuffle(UnobservedList);
        	for (int iter2 = 0; iter2 < rho*num_train; iter2++)
        	{
        		UnionList.add(UnobservedList.get(iter2));
        	}
        	UnionList.addAll(ObservedList);
        	Collections.shuffle(UnionList);
        	for (int iter2 = 0; iter2 < (rho+1)*num_train; iter2++)
        	{
        		String[] u_i = UnionList.get(iter2).split("-");
        		int u = Integer.parseInt(u_i[0]);
                int i = Integer.parseInt(u_i[1]);
                int key = Integer.parseInt(u_i[2]); // this pair in in observed set if key is 1
                if(TrainData.containsKey(u))
                	MFLogLoss(u, i, key);
        	}
        	UnionList.clear();		
        }
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * @param u
     * @param i
     * @param key
     */
    public static void MFLogLoss(int u, int i,int key)
    {           
        // ===================================================                  
        HashSet<Integer> ItemSet = TrainData.get(u);
        int ItemSetSize = ItemSet.size();    
        // ===================================================  
        
        // ----- normalization
        float normalizationFactor;
        if(key==1)
        {
        	normalizationFactor = (float) Math.pow(ItemSetSize+0.0001f, alpha);
        }
        else
        {
        	normalizationFactor = (float) Math.pow(ItemSetSize+1+0.0001f, alpha);
        }
        
        
        // --- $U_{u\cdot}^{-i}$
        float[] U_u_i = new float[d];
        // --- get similarity list of i
        HashMap<Integer,Float> sim_i = new HashMap<Integer,Float>();
        if(lambda>0)
        {
            sim_i = item2itemSimilarity.get(i);
        }
        
        // --- add similarity
        for(int i2 : ItemSet)
        {
            if (i2 != i)
            {
                float sim_i_i2 = 0;
                // if(sim_i != null && sim_i.get(i2) != null)
                if( sim_i != null && sim_i.containsKey(i2) )
                {
                    sim_i_i2 = sim_i.get(i2);
                }
                else
                {
                    sim_i_i2 = 0;
                }
                for (int f=0; f<d; f++)
                {
                    // U_u_i[f] += ( (1-lambda) + lambda*sim_i_i2 ) * W[i2][f];
                    U_u_i[f] += (1 + sim_i_i2 ) * W[i2][f];
                }
            }
        }
                
        /*float sim_i_i = 0;           
        if( sim_i != null && sim_i.containsKey(i) )
        {
            sim_i_i = sim_i.get(i);
        }
        else
        {
            sim_i_i = 0;
        }*/
        
        //
        for(int f=0; f<d; f++)
        {
            U_u_i[f] = U_u_i[f] / normalizationFactor;          
        }       
        // ===================================================

        
        // ===================================================
        // --- calculate the loss
        float r_ui = biasV[i] + biasU[u];
        for (int f=0; f<d; f++)
        {
            r_ui += (U_u_i[f] * V[i][f] + V[i][f] * U[u][f] / normalizationFactor);           
        }

        // ---------------------------------------------------

        // ---------------------------------------------------
        float eui;
        if(key==1)
        {
        	float EXP_r_ui = (float) Math.pow(Math.E, r_ui);
        	eui = 1f / (1f + EXP_r_ui);
        }
        else
        {
        	float EXP_r_ui = (float) Math.pow(Math.E, -r_ui);
        	eui = -1f / (1f + EXP_r_ui);
        }
        
        
        // ===================================================
        float[] copy_Vi = new float[d];
        float[] copy_Uu = new float[d];
        for(int f=0; f<d; f++)
        {
            copy_Vi[f] = V[i][f];
            copy_Uu[f] = U[u][f];
        }
        
        // ===================================================
        // --- update $V_{i\cdot}$      
        for (int f=0; f<d; f++)
        {
            float grad_U_u_f = -eui * copy_Vi[f] / normalizationFactor + alpha_v * U[u][f];
            U[u][f] = U[u][f] - gamma * grad_U_u_f;

            float grad_V_i_f = -eui * (copy_Uu[f]/ normalizationFactor + U_u_i[f]) + alpha_v * V[i][f];
            V[i][f] = V[i][f] - gamma * grad_V_i_f;
        }
        
        // ----- update $W_{i'\cdot}$
        for(int i2 : ItemSet)
        {
            if (i2 != i)
            {
                float sim_i_i2 = 0;
                // if(sim_i != null && sims_i.get(i2) != null)
                if( sim_i != null && sim_i.containsKey(i2) )
                {
                    sim_i_i2 = sim_i.get(i2);
                }
                else
                {
                    sim_i_i2 = 0;
                }
                for (int f=0; f<d; f++)
                {
                	W[i2][f] = W[i2][f] - gamma * (-eui*( 1 + sim_i_i2 ) * copy_Vi[f] / normalizationFactor + alpha_w * W[i2][f]);
                }
            }
        }
        
        
        // ===================================================                  
        // --- update $b_i$
        float grad_biasV_i = -eui + beta_v * biasV[i];
        biasV[i] = biasV[i] - gamma * grad_biasV_i;

        float grad_biasU_u = -eui + beta_v * biasU[u];
        biasU[u] = biasU[u] - gamma * grad_biasU_u;
        // ===================================================       
        
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @SuppressWarnings("unchecked")
    public static void testRanking(HashMap<Integer, HashSet<Integer>> TestData)
    {
        // ==========================================================
        float[] PrecisionSum = new float[topK+1];
        float[] RecallSum = new float[topK+1];
        float[] F1Sum = new float[topK+1];
        float[] NDCGSum = new float[topK+1];
        float[] OneCallSum = new float[topK+1];
        float MRRSum = 0;
        float MAPSum = 0;
        float ARPSum = 0;
        float AUCSum = 0;
        
        // --- calculate the best DCG, which can be used later
        float[] DCGbest = new float[topK+1];
        for (int k=1; k<=topK; k++)
        {
            DCGbest[k] = DCGbest[k-1];
            DCGbest[k] += 1/Math.log(k+1);
        }
            
        // --- number of test cases
        int UserNum_TestData = TestData.keySet().size();

        try {
            File writename = new File("result/predict");
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        for(int u=1; u<=n; u++)
        {
            // --- check whether the user $u$ is in the test set


            if (!TestData.containsKey(u))
                continue;
            
            // ---
            HashSet<Integer> ItemSet_u_TrainData = new HashSet<Integer>();
            if (TrainData.containsKey(u))
            {
                ItemSet_u_TrainData = TrainData.get(u);
            }
            HashSet<Integer> ItemSet_u_TestData = TestData.get(u);
            
            // --- the number of preferred items of user $u$ in the test data 
            int ItemNum_u_TestData = ItemSet_u_TestData.size();         
            
            // =========================================================        
            // --- prediction           
            HashMap<Integer, Float> item2Prediction = new HashMap<Integer, Float>();
            item2Prediction.clear();
            
            for(int i=1; i<=m; i++)
            {
                // --- (1) check whether item $i$ is in the whole item set
                // --- (2) check whether item $i$ appears in the training set of user $u$
                if ( !ItemSetWhole.contains(i)
                        || ItemSet_u_TrainData.contains(i) )
                    continue;
                                
                //compute U_u for each i that predicted, because i is unknown, use U_u
                float[] U_u = new float[d];
                HashMap<Integer, Float> sim_i = new HashMap<Integer, Float>();
                if(lambda>0)
                {
                    sim_i = item2itemSimilarity.get(i);
                }
                
                for(int i2 : ItemSet_u_TrainData)
                {
                    float sim_i_i2 = 0;
                    if( sim_i!=null && sim_i.containsKey(i2) )
                    {
                        sim_i_i2 = sim_i.get(i2);
                    }
                    else
                    {
                        sim_i_i2=0;
                    }
                    for(int f=0; f<d; f++)
                    {
                        U_u[f] +=( 1 + sim_i_i2 ) * W[i2][f];
                    }
                }
                // ----- normalization
                float normalizationFactor = (float) Math.pow(ItemSet_u_TrainData.size()+1,alpha); 
                for(int f=0; f<d; f++)
                {
                    U_u[f] = U_u[f] / normalizationFactor;
                }
                
                // --- prediction via inner product
                float pred = biasV[i] + biasU[u];
                for (int f=0; f<d; f++)
                {
                    pred += (U_u[f]*V[i][f] + V[i][f]*U[u][f]/normalizationFactor);
                } 
                item2Prediction.put(i, pred);
            }
            // --- sort
            List<Map.Entry<Integer,Float>> listY = 
                    new ArrayList<Map.Entry<Integer,Float>>(item2Prediction.entrySet());        
            Collections.sort(listY, new Comparator<Map.Entry<Integer,Float>>()
            {
                public int compare( Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2 )   
                {
                    return o2.getValue().compareTo( o1.getValue() );
                }
            });
            // ===========================================================
            // === Evaluation: TopK Result 
            // --- Extract the topK recommended items
            int k=1;
            int[] TopKResult = new int [topK+1];            
            Iterator<Entry<Integer, Float>> iter = listY.iterator();
            while (iter.hasNext())
            {
                if(k>topK)
                    break;
                
                Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
                int itemID = entry.getKey();
                float score = entry.getValue();
                TopKResult[k] = itemID;
                
                out.write(u+" "+itemID+"\n");
                out.flush();
            	
                // System.out.print(u);
                // System.out.print(" ");
                // System.out.println(itemID);
                
                k++;
            }
            // --- TopK evaluation
            int HitSum = 0;
            float[] DCG = new float[topK+1];
            float[] DCGbest2 = new float[topK+1];
            for(k=1; k<=topK; k++)
            {
                // ---
                DCG[k] = DCG[k-1];
                int itemID = TopKResult[k];
                if ( ItemSet_u_TestData.contains(itemID) )
                {
                    HitSum += 1;
                    DCG[k] += 1 / Math.log(k+1);
                }
                // --- precision, recall, F1, 1-call
                float prec = (float) HitSum / k;
                float rec = (float) HitSum / ItemNum_u_TestData;                
                float F1 = 0;
                if (prec+rec>0)
                    F1 = 2 * prec*rec / (prec+rec);
                PrecisionSum[k] += prec;
                RecallSum[k] += rec;
                F1Sum[k] += F1;
                // --- in case the the number relevant items is smaller than k 
                if (ItemSet_u_TestData.size()>=k)
                    DCGbest2[k] = DCGbest[k];
                else
                    DCGbest2[k] = DCGbest2[k-1];
                NDCGSum[k] += DCG[k]/DCGbest2[k];
                // ---
                OneCallSum[k] += HitSum>0 ? 1:0; 
            }
            // ===========================================================
            
            // ===========================================================
            // === Evaluation: Reciprocal Rank
            if (flagMRR)
            {
                int p = 1;
                iter = listY.iterator();            
                while (iter.hasNext())
                {   
                    Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
                    int itemID = entry.getKey();
                    
                    // --- we only need the position of the first relevant item
                    if(ItemSet_u_TestData.contains(itemID))                 
                        break;
    
                    p += 1;
                }
                MRRSum += 1 / (float) p;
            }
            // ===========================================================
            
            // ===========================================================
            // === Evaluation: Average Precision
            if (flagMAP)
            {
                int p = 1; // the current position
                float AP = 0;
                int HitBefore = 0; // number of relevant items before the current item
                iter = listY.iterator();            
                while (iter.hasNext())
                {   
                    Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
                    int itemID = entry.getKey();
                    
                    if(ItemSet_u_TestData.contains(itemID))
                    {
                        AP += 1 / (float) p * (HitBefore + 1);
                        HitBefore += 1;
                    }
                    p += 1;
                }
                MAPSum += AP / ItemNum_u_TestData;
            }
            // ===========================================================
            
            // ===========================================================
            // --- Evaluation: Relative Precision
            if (flagARP)
            {
                int p = 1; // the current position
                float RP = 0;           
                iter = listY.iterator();            
                while (iter.hasNext())
                {
                    Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
                    int itemID = entry.getKey();
                    
                    if(ItemSet_u_TestData.contains(itemID))
                        RP += p;
                    p += 1;
                }
                // ARPSum += RP / ItemSetWhole.size() / ItemNum_u_TestData;
                ARPSum += RP / item2Prediction.size() / ItemNum_u_TestData;
            }
            // ===========================================================
            
            // ===========================================================
            // --- Evaluation: AUC
            if (flagAUC)
            {
                int AUC = 0; 
                for (int i: ItemSet_u_TestData)
                {
                    float r_ui = item2Prediction.get(i);
                    
                    for( int j: item2Prediction.keySet() )
                    {   
                        if( !ItemSet_u_TestData.contains(j) )
                        {
                            float r_uj = item2Prediction.get(j);
                            if ( r_ui > r_uj )
                            {
                                AUC += 1;
                            }
                        }
                    }
                }
                        
                AUCSum += (float) AUC / (item2Prediction.size() - ItemNum_u_TestData) / ItemNum_u_TestData;
            }
            // ===========================================================
            
        }

        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        // =========================================================
        // --- the number of users in the test data
        System.out.println( "The number of users in the test data: " + Integer.toString(UserNum_TestData) );
        
        // --- precision@k
        for(int k=1; k<=topK; k++)
        {
            float prec = PrecisionSum[k]/UserNum_TestData;
            System.out.println("Prec@"+Integer.toString(k)+":"+Float.toString(prec));           
        }
        // --- recall@k
        for(int k=1; k<=topK; k++)
        {
            float rec = RecallSum[k]/UserNum_TestData;
            System.out.println("Rec@"+Integer.toString(k)+":"+Float.toString(rec));         
        }
        // --- F1@k
        for(int k=1; k<=topK; k++)
        {
            float F1 = F1Sum[k]/UserNum_TestData;
            System.out.println("F1@"+Integer.toString(k)+":"+Float.toString(F1));           
        }
        // --- NDCG@k
        for(int k=1; k<=topK; k++)
        {
            float NDCG = NDCGSum[k]/UserNum_TestData;
            System.out.println("NDCG@"+Integer.toString(k)+":"+Float.toString(NDCG));           
        }
        // --- 1-call@k
        for(int k=1; k<=topK; k++)
        {
            float OneCall = OneCallSum[k]/UserNum_TestData;
            System.out.println("1-call@"+Integer.toString(k)+":"+Float.toString(OneCall));          
        }
        // --- MRR
        float MRR = MRRSum/UserNum_TestData;
        System.out.println("MRR:" + Float.toString(MRR));
        // --- MAP
        float MAP = MAPSum/UserNum_TestData;
        System.out.println("MAP:" + Float.toString(MAP));
        // --- ARP
        float ARP = ARPSum/UserNum_TestData;
        System.out.println("ARP:" + Float.toString(ARP));
        // --- AUC
        float AUC = AUCSum/UserNum_TestData;
        System.out.println("AUC:" + Float.toString(AUC));
        // =========================================================
    }
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++    
}
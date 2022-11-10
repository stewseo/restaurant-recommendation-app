package org.example.yelp.fusion.client.businesses;

import org.example.yelp.fusion.client.*;
import org.junit.jupiter.api.*;

public class VectorSearchTest extends AbstractRequestTestCase {


    //  Example model: msmarco-MiniLM-L-12-v3 from Hugging Face
    @Test
    void deployPublicTextEmbeddingModelTest(){

    }

    //  MS MARCO Passage Ranking dataset
    @Test
    void loadInitialDataTest(){

    }

    //Uses a pre-trained data frame analytics model or a model deployed for
    // natural language processing tasks to infer against the data that is being ingested in the pipeline.
    @Test
    void putInferenceProcessorTest(){
        
    }
    
    //Reindex documents from the collection index into the new collection-with-embeddings index by pushing documents through text-embeddings pipeline,
    // so that documents in the collection-with-embeddings index have an additional field for passages’ embeddings
    @Test
    void reIndexDataTest() {
        
    }

    @Test
    void vectorSimilaritySearchTest() {
        
    }

    static boolean isIdentity(int mat[][])
    {
         int n = mat.length;
        System.out.println(n);
        for (int row = 0; row < n; row++)
        {
            for (int col = 0; col < n; col++)
            {
                if (row == col && mat[row][col] == 1)
                    System.out.print(1);

                else if (row != col && mat[row][col] == 0)
                    System.out.print(0);

                System.out.print(" ");

            }
            System.out.println();
        }
        return true;
    }

    // Driver Code
    public static void main(String args[])
    {
        int N = 4;
        int mat[][] = {{1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};

        if (isIdentity(mat))
            System.out.println("Yes ");
        else
            System.out.println("No ");
    }
}


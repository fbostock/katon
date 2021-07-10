package fjdb.images.pca;

import fjdb.images.MainImage;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.awt.image.BufferedImage;

public class Play {

    public static void main(String[] args) {
//        double[][] array = {{1.12, 2.05, 3.12}, {5.56, 6.28, 8.94}, {10.2, 8.0, 20.5}};
//        LinkedList<Vector> rowsList = new LinkedList<>();
//        for (int i = 0; i < array.length; i++) {
//            Vector currentRow = Vectors.dense(array[i]);
//            rowsList.add(currentRow);
//        }
//        JavaRDD<Vector> rows = JavaSparkContext.fromSparkContext(new JavaSparkContext().sc()).parallelize(rowsList);
//
//// Create a RowMatrix from JavaRDD<Vector>.
//        RowMatrix mat = new RowMatrix(rows.rdd());
//
//// Compute the top 3 principal components.
//        Matrix pc = mat.computePrincipalComponents(3);
//        RowMatrix projected = mat.multiply(pc);
//        System.out.println(projected);


/*        SparkConf conf = new SparkConf().setAppName("PCAExample").setMaster("local");
        try (JavaSparkContext sc = new JavaSparkContext(conf)) {
            //Create points as Spark Vectors
            List<Vector> vectors = Arrays.asList(
                    Vectors.dense( -1.0, -1.0 ),
                    Vectors.dense( -1.0, 1.0 ),
                    Vectors.dense( 1.0, 1.0 ));

            //Create Spark MLLib RDD
            JavaRDD<Vector> distData = sc.parallelize(vectors);
            RDD<Vector> vectorRDD = distData.rdd();

            //Execute PCA Projection to 2 dimensions
            PCA pca = new PCA(2);
            PCAModel pcaModel = pca.fit(vectorRDD);
            Matrix matrix = pcaModel.pc();

            System.out.println(matrix);*/


        double[][] pointsArray = new double[][] {
                new double[] { -1.0, -1.0 },
                new double[] { -1.0, 1.0 },
                new double[] { 1.0, 1.0 } };

//create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(pointsArray);

//create covariance matrix of points, then find eigen vectors
//see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues

        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);

        RealMatrix eigenVectorMatrix = ed.getV();

        //T = XV, where V is the matrix whose columns are eigenvectors of the covariance matrix. X is the input dataset,
        //and T is the dataset where entries in X have been rotated into principal component space.
        //Once in principal component space, we could measure the square distance between the rotated model vectors, and the input
        //candidate images as a means of identifying best matches.
        //TODO check how face recognition is done with PCA.

        System.out.println(ed.getImagEigenvalues());

        /*
        Model from main image. Imagine its a 50 by 50 pixel image. We break it up into 10 by 10 tiles. We therefore have
        25 of these tiles. Each tile is an element, or vector, in our model. We thus have a model of 25 vectors.
        Each tile has 100 pixels. Let's assume we have a simple number for each pixel. The dimension of our model therefore
        is 100.

         */

    }

    public static void play(MainImage mainImage) {
        BufferedImage bufferedImage = mainImage.getBufferedImage();

        //get tiles from image


    }
}

/**
 * Clase que representa un Tour, contiene el tour y su peso.
 * @author Gaitan Sandra
 */
public class Tour implements Comparable<Tour>{
    private int[] trayectoria;//tour
    private double aptitud;//peso del tour
    private boolean muto;
    
    /**
     * Constructor de clase
     * @param tour int[]
     * @param peso double
     */
    public Tour(int[] tour, double peso) {
        this.trayectoria = tour;
        this.aptitud = peso;
        this.muto = false;
    }

    public boolean isMuto() {
        return muto;
    }

    public void setMuto(boolean muto) {
        this.muto = muto;
    }
    
    

    /**
     * Devuelve un arreglo que representa un tour.
     * @return int[]
     */
    public int[] getTrayectoria() {
        return trayectoria;
    }

    /**
     * Asigna el tour
     * @param trayectoria 
     */
    public void setTrayectoria(int[] trayectoria) {
        this.trayectoria = trayectoria;
    }

    /**
     * Recibe el peso del tour
     * @param pesos double[]
     */
    public void setPeso(double[][] red){
        double peso = 0.0;
        for(int i = 0; i < trayectoria.length-1; i++){
   //System.out.println("i " + i +" "+ trayectoria[i] + " i+1 "+(i+1) +" "+ trayectoria[i+1]);
            peso += red[trayectoria[i]][trayectoria[i+1]];
        }
        peso += red[trayectoria[trayectoria.length-1]][trayectoria[0]];
        this.aptitud = peso;
    }
    
    /**
     * Devuelve el peso del tour
     * @return double
     */
    public double getPeso(){
        return this.aptitud;
    }
    
    
    @Override
    public int compareTo(Tour o) {
        if(this.aptitud <= o.aptitud) return -1;
        else return 0;
    }
    
    @Override
    public String toString(){
        String cad = "", cad2 = "";
        for(int i = 0; i < this.trayectoria.length; i++){
            cad += this.trayectoria[i] + " ";
        }
        if(muto) 
            cad2 += " muto: si";
        else 
            cad2 += " muto: no";
        return "{tour: (" + cad + ") peso: " + this.aptitud + cad2+"}\n";

    }

}

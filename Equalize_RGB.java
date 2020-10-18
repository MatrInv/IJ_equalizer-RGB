// Importation des paquets nécessaires. Le plugin n'est pas lui-même un paquet (pas de mot-clé package)
import ij.*; 							// pour classes ImagePlus et IJ
import ij.plugin.filter.PlugInFilter; 	// pour interface PlugInFilter
import ij.process.*; 					// pour classe ImageProcessor
import java.awt.*; 						// pour classe Rectangle

// Nom de la classe = nom du fichier.  Implémente l'interface PlugInFilter
public class Equalize_RGB implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		// Accepte tous types d'images, piles d'images et RoIs, même non rectangulaires
		return DOES_8G+DOES_RGB+DOES_STACKS+SUPPORTS_MASKING;
	}

	public void run(ImageProcessor ip) {
 

		Rectangle r = ip.getRoi(); // Région d'intérêt sélectionnée (r.x=r.y=0 si aucune)

		ColorProcessor cp = ip.convertToColorProcessor(); 

		int distrib = 256;

		//on crée un tableau de taille 256 contenant qui à un niveau de gris associe le nombre de pixels correspondant
		int histo[] = new int[distrib];
		Color color;
		int I[][] = new int[r.width][r.height];

		//on établit déjà un tableau I où I[x][y] correspond à l'intensité du pixel (x,y)
		//on parcours tous les pixels de r en incrémentant la valeur indexée par son niveau de gris(qu'on a calculé puis stocké dans I)
		//on obtient ainsi l'histogramme de r ainsi qu'un tableau d'intensité de l'image;

		for (int y=r.y; y<(r.y+r.height); y++)
			for (int x=r.x; x<(r.x+r.width); x++){
				color = cp.getColor(x,y);

				I[x][y] = (int) ( (0.3*(double)color.getRed()) + (0.59*(double)color.getGreen()) + (0.11*(double)color.getBlue()) ) ;

				histo[I[x][y]]++;
			}

		//pour chaque valeur d'intensité, on évalue la proportion de r ayant une intensité inférieure

		double R[] = new double[distrib];
		int p = 0;

		for(int i=0;i<distrib;i++){

			p+=histo[i];

			R[i] = ((double)p)/(double)(r.width*r.height);

		}

		int coef;
		int rgb[] = new int[3];
		//calcul de la transformation
		for (int yt=r.y; yt<(r.y+r.height); yt++)
			for (int xt=r.x; xt<(r.x+r.width); xt++){
				//IJ.log("\n"+ip.get(xt,yt));
				coef = (int)(R[ I[xt][yt] ]*(distrib-1));
				rgb[0] = coef;
				rgb[1] = coef;
				rgb[2] = coef;

				ip.putPixel(xt,yt,rgb);
			}

	}

}

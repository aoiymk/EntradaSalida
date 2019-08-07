package Geometria;

import Herramientas.DataFrame;

/**
 * Created by ecarreno on 14-12-2016.
 */
public class Correccion_Citymovil {


    public static DataFrame Correccion_pulsos(DataFrame input, String columna_x, String columna_y){

        input.InsertaNuevaColumnaVacia("f_lat_latitud");
        input.InsertaNuevaColumnaVacia("f_lon_longitud");


        for ( int i =0;i<input.size();i++){
            Coordenada c = new Coordenada( Double.parseDouble( input.getDouble(columna_x,i).toString() ),
                                            Double.parseDouble( input.getDouble(columna_y,i).toString() ), true);

            c.toWGS84();
            input.set("f_lat_latitud",i,c.getLat());
            input.set("f_lon_longitud",i,c.getLon());

        }
        input.BorrarColumna(columna_x);
        input.BorrarColumna(columna_y);


     return input;
    }

    public static class Coordenada {

        public  double latitud;//EN WGS84;
        public  double longitud;//EN WGS84;

        double lat_interna;
        double lon_interna;

        public double x;//EN UTM19
        public  double y;//EN UTM19

        double inFi;
        double inLamda;
        double outfi;
        double outLamda;

        boolean isUTM19;

        public double getLat() {
            return latitud;
        }

        public double getLon() {
            return longitud;

        }

        public Coordenada(double x, double y, boolean isUTM19) {

            lat_interna=x;
            lon_interna=y;

            this.inFi            = 0;
            this.inLamda        = 0;
            this.outfi            = 0;
            this.outLamda        = 0;
            this.isUTM19 =isUTM19;

            if(isUTM19) {
                this.x = x;
                this.y = y;
            }

            //Recibe coordenas X e Y, calcula  Lat y long.
            else
            {
                this.latitud = x;
                this.longitud = y;
                toUTM19();
            }

        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;

        }

        public void setX(double x) {
            this.x = x;
            this.toWGS84();
        }

        public void setY(double y) {
            this.y = y;
            this.toWGS84();
        }


        private void toUTM19()
        {
            coordDirecta();
        }

        public void toWGS84() {coordInverso();}

        private void cambioDatum()
        {
            //Input WGS84.-->Output SAD69
            double pi = Math.PI;
            double fi = pi*inFi/180.0;
            double lamda = pi*inLamda/180.0;
            double a = 6378137.0;
            double b = 6356752.31424518;
            double N = Math.pow(a,2)/ Math.sqrt((Math.pow(a,2)* Math.pow(Math.cos(fi),2))+(Math.pow(b,2)* Math.pow(Math.sin(fi),2)));
            //printf("Cambiando Datum:\nN=%f\n",N);

            /****************************************
             * Referencia de la altura:
             * http://www.gpsglobal.com.br/Artigos/sisref.pdf
             ******************************************/


            double h = 763.28;//Altura--->Confirmar

            double X = (N+h)* Math.cos(fi)* Math.cos(lamda);
            double Y = (N+h)* Math.cos(fi)* Math.sin(lamda);
            double Z = (((Math.pow(b,2)/ Math.pow(a,2))*N)+h)* Math.sin(fi);
            // XX=X' y asi para YY y ZZ
            //Aqu� se escoge los datos para la transformaci�n dependiendo del valor de la latitud: fi
            double XX = 75.0 + X;
            double YY = 1.0 + Y;//-30.0+Y;
            double ZZ = 44.0 + Z;
            inFi*=-1;
//        printf("Comparando fi= %d\n\n",(int)inFi);
            if((int)inFi > 17 && (int)inFi < 32)
            {
                XX = 59.0 + X;
                YY = 11.0 + Y;
                ZZ = 52.0 + Z;
            }
            else if((int)inFi >= 32 && (int)inFi < 36)
            {
                XX = 64.0 + X;
                YY = 0.0 + Y;
                ZZ = 32.0 + Z;
            }
            else if((int)inFi >= 36 && (int)inFi < 44)
            {
                XX = 72.0 + X;
                YY = -10.0 + Y;
                ZZ = 32.0 + Z;
            }
            a = 6378160.00000;//Semieje Mayor
            b = 6356774.7192;//Semieje Menor
            N = Math.pow(a,2)/ Math.sqrt(Math.pow(a,2)* Math.pow(Math.cos(fi),2)+ Math.pow(b,2)* Math.pow(Math.sin(fi),2));
            double e2 = (Math.pow(a,2)- Math.pow(b,2))/ Math.pow(a,2);
            double ee2 = (Math.pow(a,2)- Math.pow(b,2))/ Math.pow(b,2);
            double p = Math.sqrt(Math.pow(XX,2)+ Math.pow(YY,2));
            double teta = Math.atan2(ZZ*a,(p*b));
            lamda = Math.atan2(YY,XX);
            fi = Math.atan2((ZZ+ee2*b* Math.pow(Math.sin(teta),3)),(p-e2*a* Math.pow(Math.cos(teta),3)));
            //printf("lamnda =%f\nfi=%f\n",lamda*180/pi,fi*180/pi);
            outfi = fi*180.0/pi;
            outLamda = lamda*180.0/pi;
        }

        void coordDirecta()
        {
            double pi = Math.PI;
            double fiDicimal;
            double lamdaDecimal;
            inFi = longitud;
            inLamda = latitud;
            this.cambioDatum();
            fiDicimal = outfi;
            lamdaDecimal = outLamda;
            //printf("Cambiod Datum:\n\t INTPUT=%f , %f\n\t OUTPUT: %.8f , %.8f",paramFi,paramLamda,fiDicimal,lamdaDecimal);
            double fi = pi*fiDicimal/180.0;
            double lamda = pi*lamdaDecimal/180.0;
            double a = 6378160.00000;//Semieje Mayor
            double b = 6356774.7191953100;//Semieje Menor
            //double e=(Math.sqrt(Math.pow(a,2)-Math.pow(b,2)))/a;
            double ee = (Math.sqrt(Math.pow(a,2)- Math.pow(b,2)))/b;
            double ee2 = Math.pow(ee,2);
            double c = Math.pow(a,2)/b;
            //double aplanamiento=(a-b)/a;
            int huso = (int)lamdaDecimal/6+31;
            huso = 19;//Tiene que ser fijo
            //printf("HUSO: %d\n",huso);
            //printf("lamda0=%f\n",(double)(huso*6-183));
            double lamda0 = (double)(huso*6-183)*pi/180.0;
            //printf("lamda=%f\n",fiDicimal);
            double deltaLamda = lamda - lamda0;
            //printf("deltaLamda=%f\n",deltaLamda);
            double A = Math.cos(fi)* Math.sin(deltaLamda);
            //printf("A=%f\n",A);
            double E = 0.5* Math.log((1+A)/(1-A));
            //printf("E=%f\n",E);
            double eta = Math.atan2(Math.tan(fi), Math.cos(deltaLamda))-fi;
            //printf("eta=%f\n",eta);
            double nu = c/ Math.sqrt((1+ee2* Math.pow(Math.cos(fi),2)))*0.9996;
            //printf("nu=%f\n",nu);
            double C = ee2*0.5*E*E* Math.pow(Math.cos(fi),2);
            //printf("C=%.20f\n",C);
            double A1 = Math.sin(2.0*fi);
            //printf("A1=%f\n",A1);
            double A2 = A1* Math.pow(Math.cos(fi),2);
            //printf("A2=%f\n",A2);
            double J2 = fi+A1*0.5;
            //printf("J2=%f\n",J2);
            double J4 = (3*J2+A2)/4.0;
            //printf("J4=%f\n",J4);
            double J6 = (5*J4+A2* Math.pow(Math.cos(fi),2))/3.0;
            //printf("J6=%f\n",J6);
            double alfa = 3.0/4.0*ee2;
            //printf("alfa=%f\n",alfa);
            double beta = 5.0/3.0* Math.pow(alfa,2);
            //printf("beta=%f\n",beta);
            double y = 35.0/27.0* Math.pow(alfa,3);
            //printf("y=%.20f\n",y);
            double B0 = 0.9996*c*(fi-alfa*J2+beta*J4-y*J6);
            //printf("B0=%10f\n",B0);
            double X = E*nu*(1+C/3.0)+500000.0;
            double Y = nu*eta*(1+C)+B0+10000000.0;
            //printf("X = %f Y=%f\n",X,Y);
            this.x = X;
            this.y = Y;
        }


        void coordInverso()
        {
            double pi = Math.PI;
            double a=6378160.00000;//Semieje Mayor
            double b=6356774.7191953100;//Semieje Menor
//        double e=(sqrt(pow(a,2)-pow(b,2)))/a;
            double ee=(Math.sqrt(Math.pow(a,2)- Math.pow(b,2)))/b;
            double ee2= Math.pow(ee,2);
            double c= Math.pow(a,2)/b;
//        double aplanamiento=(a-b)/a;
            int huso=19;
            double lamda0=huso*6-183;
            //
            double X=this.x-500000.0;
            double Y=this.y-10000000.0;
            //
            double fi=Y/(6366197.724*0.9996);
            //printf("fi=%f\n",fi);
            double nu=c/ Math.sqrt((1+ee2* Math.pow(Math.cos(fi),2)))*0.9996;
            //printf("nu=%f\n",nu);
            a=X/nu;
            //printf("a=%f\n",a);
            double A1= Math.sin(2.0*fi);
            //printf("A1=%f\n",A1);
            double A2=A1* Math.pow(Math.cos(fi),2);
            //printf("A2=%f\n",A2);
            double J2=fi+A1*0.5;
            //printf("J2=%f\n",J2);
            double J4=(3*J2+A2)/4.0;
            //printf("J4=%f\n",J4);
            double J6=(5*J4+A2* Math.pow(Math.cos(fi),2))/3.0;
            //printf("J6=%f\n",J6);
            double alfa=3.0/4.0*ee2;
            //printf("alfa=%f\n",alfa);
            double beta=5.0/3.0* Math.pow(alfa,2);
            //printf("beta=%f\n",beta);
            double y=35.0/27.0* Math.pow(alfa,3);
            //printf("y=%.20f\n",y);
            double B0=0.9996*c*(fi-alfa*J2+beta*J4-y*J6);
            //printf("B0=%10f\n",B0);
            b=(Y-B0)/nu;
            double C=ee2*0.5*a*a* Math.pow(Math.cos(fi),2);
            //printf("C=%10f\n",C);
            double epsilon=a*(1-C/3);
            //printf("epsilon=%10f\n",epsilon);
            double eta=b*(1-C)+fi;
            //printf("eta=%10f\n",eta);
            double senhepsilon= Math.sinh(epsilon);
            //printf("senhepsilon=%10f\n",senhepsilon);
            double DLambda= Math.atan2(senhepsilon, Math.cos(eta));
            //printf("DLambda=%10f\n",DLambda);
            double tau= Math.atan2(Math.cos(DLambda)* Math.tan(eta),1);
            //printf("tau: %f\n",tau);
            double lamdaTemp=(DLambda)*180.0/pi+lamda0;
            double fiTemp=(fi+(1+ee2* Math.pow(Math.cos(fi),2)-3.0/2.0*ee2* Math.sin(fi)* Math.cos(fi)*(tau-fi))*(tau-fi))*180.0/pi;

            double outFi;
            double outLambda;
            cambioDatumInverso(fiTemp,lamdaTemp);



        }

        void cambioDatumInverso(double inFi,double inLamda)
        {
            //Input WGS84.-->Output SAD69
            double pi = Math.PI;
            double fi=pi*inFi/180.0;
            double lamda=pi*inLamda/180.0;

            double a=6378160.00000;//Semieje Mayor
            double b=6356774.7192;//Semieje Menor
            double N= Math.pow(a,2)/ Math.sqrt((Math.pow(a,2)* Math.pow(Math.cos(fi),2))+(Math.pow(b,2)* Math.pow(Math.sin(fi),2)));

            /****************************************
             * Referencia de la altura:
             * http://www.gpsglobal.com.br/Artigos/sisref.pdf
             ******************************************/
            double h=763.28;//Altura--->Confirmar

            double X=(N+h)* Math.cos(fi)* Math.cos(lamda);
            double Y=(N+h)* Math.cos(fi)* Math.sin(lamda);
            double Z=(((Math.pow(b,2)/ Math.pow(a,2))*N)+h)* Math.sin(fi);
            // XX=X' y asi para YY y ZZ
            //Aqu se escoge los datos para la transformacin dependiendo del valor de la latitud: fi
            double XX= -75.0+X;
            double YY= -1.0+Y;//-30.0+Y;
            double ZZ= -44.0+Z;
            inFi*=-1;
            if((int)inFi>17 && (int)inFi<32)
            {
                XX= -59.0+X;
                YY= -11.0+Y;
                ZZ= -52.0+Z;
            }
            else if((int)inFi>=32 && (int)inFi<36)
            {
                XX= -64.0+X;
                YY= -0.0+Y;
                ZZ= -32.0+Z;

            }
            else if((int)inFi>=36 && (int)inFi<44)
            {
                XX= -72.0+X;
                YY= 10.0+Y;
                ZZ= -32.0+Z;
            }
            a=6378137.0;
            b=6356752.31424518;

            N= Math.pow(a,2)/ Math.sqrt(Math.pow(a,2)* Math.pow(Math.cos(fi),2)+ Math.pow(b,2)* Math.pow(Math.sin(fi),2));
            double e2=(Math.pow(a,2)- Math.pow(b,2))/ Math.pow(a,2);
            double ee2=(Math.pow(a,2)- Math.pow(b,2))/ Math.pow(b,2);
            double p= Math.sqrt(Math.pow(XX,2)+ Math.pow(YY,2));
            double teta= Math.atan2(ZZ*a,(p*b));
            lamda= Math.atan2(YY,XX);
            fi= Math.atan2((ZZ+ee2*b* Math.pow(Math.sin(teta),3)),(p-e2*a* Math.pow(Math.cos(teta),3)));
            //printf("lamnda =%f\nfi=%f\n",lamda*180/pi,fi*180/pi);
            //printf("h=%.10f\n",p/cos(inFi)-N);
            double outfi=fi*180.0/pi;
            double  outLamda=lamda*180.0/pi;
            this.longitud=outLamda;
            this.latitud=outfi;
        }


    }


}

package edu.bk.thesis.biodiary.core

import android.content.Context
import android.util.Log
import org.tensorflow.contrib.android.TensorFlowInferenceInterface

class CombineScore(context: Context)
{

    private val MODEL_FILE = "file:///android_asset/frozen_mlpThesis.pb"
    private val INPUT_NODES = arrayOf("X:0")
    private val OUTPUT_NODES = arrayOf("y_pred:0")
    private val INPUT_DIM = longArrayOf(1, 512)

    var inferenceInterface: TensorFlowInferenceInterface =
            TensorFlowInferenceInterface(context.assets, MODEL_FILE)

    var inputX = FloatArray(INPUT_DIM[1].toInt())
    var outputYPred = LongArray(1)

    private fun mock()
    {
        inputX = floatArrayOf(7.73866773e-02f, 2.08748057e-02f, -8.79118405e-03f,
                              2.34693140e-02f,
                              8.31711944e-03f, -3.78261916e-02f, -5.49587160e-02f,
                              1.86432656e-02f,
                              5.14071472e-02f, 5.25085069e-02f, 6.06825389e-02f,
                              -1.15232700e-02f,
                              2.99280975e-02f, 4.90209423e-02f, 7.61021487e-03f,
                              -5.72820380e-02f,
                              3.60535900e-03f, 2.67865676e-02f, 1.80564020e-02f,
                              -9.74439841e-04f,
                              4.44661884e-04f, -8.69778022e-02f, 1.26043689e-02f,
                              -2.94380262e-02f,
                              -4.24486250e-02f, -5.32183051e-02f, -6.34916052e-02f,
                              2.28204615e-02f,
                              -3.93402064e-03f, 1.41341519e-03f, 1.58964656e-02f,
                              -1.33061670e-02f,
                              8.06771312e-03f, -1.93933528e-02f, 1.34420563e-02f,
                              -9.30295885e-02f,
                              -2.88487859e-02f, 9.14433878e-03f, -3.38294357e-02f,
                              3.17275673e-02f,
                              -8.79612267e-02f, 3.34947705e-02f, -7.96381161e-02f,
                              1.08075880e-01f,
                              7.92260021e-02f, 4.70650010e-02f, 3.37211564e-02f,
                              -6.78620394e-03f,
                              8.04169010e-03f, -4.95086014e-02f, -3.80821005e-02f,
                              -6.81825951e-02f,
                              1.71304606e-02f, 3.24451439e-02f, -4.39198688e-02f,
                              3.16186063e-02f,
                              -1.03792315e-02f, 1.14946421e-02f, -6.30924180e-02f,
                              -3.83536778e-02f,
                              -2.60588955e-02f, -2.75699254e-02f, -9.36856791e-02f,
                              -2.82198805e-02f,
                              -2.07427167e-03f, -5.97036630e-03f, 3.06022428e-02f,
                              1.12505106e-03f,
                              -1.53364968e-02f, -1.60472945e-03f, 5.10786101e-02f,
                              4.64991070e-02f,
                              -1.02484427e-01f, -4.72554564e-03f, 9.26353224e-03f,
                              -9.15895477e-02f,
                              -2.01987326e-02f, 3.54649238e-02f, 3.45374458e-02f,
                              7.52161294e-02f,
                              -1.70101188e-02f, 7.92356506e-02f, 1.02042956e-02f,
                              3.01969573e-02f,
                              -6.79757074e-02f, 7.41958395e-02f, 2.84178328e-04f,
                              7.46857375e-02f,
                              -7.96697475e-03f, 3.15365195e-02f, -2.38163047e-03f,
                              -3.65101956e-02f,
                              8.99730064e-03f, -5.84297515e-02f, 2.64497250e-02f,
                              6.64868578e-02f,
                              6.14284463e-02f, 9.65369493e-02f, 1.44723365e-02f, 7.63331447e-03f,
                              -4.51451130e-02f, 4.13566940e-02f, 5.52828424e-03f,
                              -1.18414377e-04f,
                              2.17363723e-02f, 3.82349193e-02f, -5.73668629e-02f,
                              8.01126566e-03f,
                              -6.34201691e-02f, 2.60329712e-03f, 6.27594516e-02f,
                              -5.24950819e-03f,
                              -4.40185629e-02f, -3.82128656e-02f, 7.95496348e-03f,
                              6.48627058e-02f,
                              1.31754175e-01f, 2.41443831e-02f, -1.95903741e-02f,
                              4.22706939e-02f,
                              -1.37868468e-02f, 6.33404106e-02f, -2.11837869e-02f,
                              4.78805378e-02f,
                              3.23857740e-02f, 1.59354080e-02f, -1.65401306e-02f,
                              -5.25294207e-02f,
                              -1.04127740e-02f, -5.39095998e-02f, -2.74433158e-02f,
                              3.36997285e-02f,
                              8.71103480e-02f, 1.48917614e-02f, 1.04654104e-01f,
                              -5.56554198e-02f,
                              -3.55429761e-02f, -3.15806195e-02f, 3.53398807e-02f,
                              2.16230303e-02f,
                              -6.43954128e-02f, 3.78613807e-02f, -5.36634773e-02f,
                              -1.78383831e-02f,
                              6.26553819e-02f, 7.96292946e-02f, -4.03603092e-02f,
                              5.73603325e-02f,
                              1.37239899e-02f, -1.92612726e-02f, 1.83071452e-03f,
                              1.47296544e-02f,
                              -2.62847841e-02f, 4.55080010e-02f, -4.67314869e-02f,
                              -2.60900967e-02f,
                              -3.01925633e-02f, -1.67380255e-02f, 2.94211768e-02f,
                              -7.70406947e-02f,
                              1.35429471e-03f, 2.54961867e-02f, 5.30509511e-03f, 3.37395519e-02f,
                              -3.47898491e-02f, 1.21007739e-02f, 1.08781802e-02f,
                              1.10390587e-02f,
                              2.67119147e-02f, 1.00166574e-01f, -1.67491343e-02f,
                              1.46204885e-02f,
                              -7.47320661e-03f, -3.21415626e-02f, -4.89666872e-02f,
                              3.29830796e-02f,
                              -4.33163671e-03f, 5.51513322e-02f, 2.35801898e-02f,
                              -7.50014037e-02f,
                              9.42727700e-02f, -2.62779333e-02f, -3.52910422e-02f,
                              8.41008201e-02f,
                              1.23394184e-01f, 5.27114384e-02f, -2.14556418e-02f,
                              1.70145892e-02f,
                              6.06894419e-02f, 3.58429807e-03f, 3.20081552e-03f,
                              -2.70131510e-02f,
                              2.15259660e-02f, 4.40059081e-02f, -8.47045332e-03f,
                              1.32279005e-02f,
                              -1.41166672e-02f, -6.43004924e-02f, 6.38629775e-04f,
                              1.52943311e-02f,
                              8.39005411e-02f, -2.31786296e-02f, 2.75750291e-02f,
                              -5.13908220e-04f,
                              8.71244702e-04f, 6.12969734e-02f, -1.84303001e-02f,
                              1.25333127e-02f,
                              8.02877918e-02f, 3.82334776e-02f, -6.67411610e-02f,
                              -1.29713463e-02f,
                              -4.12101299e-02f, -2.49425657e-02f, 2.45197285e-02f,
                              4.34477590e-02f,
                              -1.84778881e-03f, 1.66433468e-03f, 7.14783615e-04f,
                              1.72458440e-02f,
                              -2.55990191e-03f, 2.30531562e-02f, -2.57081073e-02f,
                              1.32997874e-02f,
                              -6.62396997e-02f, 2.72840392e-02f, 4.47315350e-02f,
                              5.61396293e-02f,
                              -4.86464314e-02f, 1.14979809e-02f, -6.05430640e-02f,
                              -4.21303213e-02f,
                              4.67958860e-02f, -5.21188267e-02f, -8.33167601e-03f,
                              6.99567348e-02f,
                              -1.06543619e-02f, 2.81312726e-02f, 9.82110873e-02f,
                              3.96624692e-02f,
                              1.00244377e-02f, 1.14476725e-01f, 2.50303950e-02f,
                              -3.82484272e-02f,
                              -4.41876426e-02f, -6.65919557e-02f, -2.07740883e-03f,
                              1.29036829e-02f,
                              1.46776903e-02f, 7.07418025e-02f, 2.90963110e-02f, 3.14580090e-02f,
                              4.60216254e-02f, -1.38706947e-02f, 2.60046218e-03f,
                              7.22590685e-02f,
                              6.69793859e-02f, 4.90626916e-02f, -6.61770173e-04f,
                              -1.94142982e-02f,
                              -1.74174551e-02f, -9.46394820e-03f, 4.85620322e-03f,
                              1.14157880e-02f,
                              1.15738157e-02f, -1.42861735e-02f, 3.60933654e-02f,
                              3.23107243e-02f,
                              7.31735548e-04f, -4.32017259e-02f, -6.43543452e-02f,
                              2.29161773e-02f,
                              3.23279202e-02f, 5.34207560e-02f, -6.42350391e-02f,
                              -5.29594161e-02f,
                              2.74372511e-02f, 1.11037884e-02f, 3.80747095e-02f,
                              -6.72462210e-02f,
                              -1.83340125e-02f, 6.11676872e-02f, 1.72246508e-02f,
                              3.63573916e-02f,
                              1.13193862e-01f, 3.95098329e-02f, 7.74388313e-02f, 3.66491936e-02f,
                              -7.97639638e-02f, -4.93087843e-02f, 1.33848172e-02f,
                              4.96441266e-03f,
                              6.25759438e-02f, 6.79186359e-02f, -1.08193411e-02f,
                              -2.34552287e-02f,
                              7.18237250e-04f, 5.26055619e-02f, 8.63344222e-02f,
                              -5.53801470e-02f,
                              -3.84342447e-02f, 2.34129131e-02f, -6.12507202e-02f,
                              -1.34010157e-02f,
                              1.13456845e-02f, 2.38102172e-02f, 5.51904477e-02f,
                              -4.20867465e-02f,
                              2.77017429e-03f, 1.07615232e-01f, -7.00339228e-02f,
                              4.39683832e-02f,
                              7.66508058e-02f, 2.62259077e-02f, 1.89386494e-02f,
                              -2.45907903e-02f,
                              -1.46527253e-02f, 2.84019448e-02f, 3.03542688e-02f,
                              1.25655392e-02f,
                              -4.05500941e-02f, -4.22525126e-03f, 2.19897479e-02f,
                              -2.61512306e-02f,
                              -1.46144582e-02f, -7.48523846e-02f, -2.32652202e-02f,
                              1.29768569e-02f,
                              3.33205424e-02f, 2.13474724e-02f, -7.77745340e-03f,
                              9.17813554e-02f,
                              -2.58031711e-02f, 1.22125652e-02f, -1.81769561e-02f,
                              2.22856775e-02f,
                              -8.21094513e-02f, 1.34692974e-02f, 1.90977864e-02f,
                              -2.42203530e-02f,
                              5.05023003e-02f, 2.99330149e-02f, -3.94426938e-03f,
                              5.76575994e-02f,
                              -4.46860828e-02f, 1.95263978e-02f, -6.38109148e-02f,
                              7.24099018e-03f,
                              2.21223459e-02f, 3.07692718e-02f, 4.81084399e-02f,
                              -1.32543603e-02f,
                              -1.07152890e-02f, 2.36100275e-02f, 5.28115407e-02f,
                              -2.74239369e-02f,
                              3.29302363e-02f, 5.04599437e-02f, 1.23415953e-02f,
                              -5.33642769e-02f,
                              -5.78126498e-02f, 7.12058414e-03f, -1.93905793e-02f,
                              1.43976593e-02f,
                              -3.52999568e-02f, -7.51000792e-02f, -7.59195769e-03f,
                              -3.51649272e-04f,
                              5.54605424e-02f, 1.20084593e-02f, -4.59491886e-04f,
                              -6.73504034e-03f,
                              -1.39308227e-02f, -3.44905667e-02f, 1.11783063e-02f,
                              1.84668396e-02f,
                              3.87581140e-02f, 1.17211118e-02f, -2.75179818e-02f,
                              -5.18285297e-02f,
                              1.80501677e-02f, -1.01190828e-01f, -2.60212421e-02f,
                              2.93620974e-02f,
                              -7.24793822e-02f, -9.26717557e-03f, -1.75741073e-02f,
                              -3.23417671e-02f,
                              -1.70975574e-03f, -3.69428881e-02f, -3.90731134e-02f,
                              2.03582793e-02f,
                              3.98865826e-02f, 5.20077534e-02f, 1.90014206e-02f, 9.28669795e-03f,
                              3.64513397e-02f, 2.01699175e-02f, 5.97584583e-02f,
                              -2.23524570e-02f,
                              1.86595097e-02f, 4.99830730e-02f, 5.91496788e-02f,
                              -9.01858415e-03f,
                              7.80157074e-02f, -6.16169814e-03f, -3.30867246e-02f,
                              -3.26865129e-02f,
                              -5.07729836e-02f, -5.19514605e-02f, 1.76852122e-02f,
                              2.25405898e-02f,
                              -8.59098956e-02f, -3.00189238e-02f, 5.56941144e-02f,
                              7.70791918e-02f,
                              5.96689507e-02f, 2.27362663e-02f, 2.85463431e-03f, 1.90398339e-02f,
                              6.40549362e-02f, 4.40327860e-02f, 2.03334671e-02f, 9.66366455e-02f,
                              -4.97532226e-02f, 5.72577976e-02f, -7.47503527e-03f,
                              -5.46103828e-02f,
                              -8.13329518e-02f, -5.23850806e-02f, -5.10728406e-03f,
                              2.20100749e-02f,
                              -2.97496710e-02f, -2.15749862e-03f, 8.54194909e-03f,
                              4.94007021e-02f,
                              -2.58127768e-02f, -5.34321275e-03f, -6.61652815e-03f,
                              2.67319046e-02f,
                              2.81805135e-02f, -1.95151120e-02f, 1.25678368e-02f,
                              7.36423284e-02f,
                              3.03630512e-02f, 2.43516676e-02f, 2.43062917e-02f,
                              -9.20087174e-02f,
                              4.86642442e-04f, -1.32971583e-02f, -1.02901915e-02f,
                              9.07957181e-02f,
                              -2.26186700e-02f, 5.36823235e-02f, 2.38157418e-02f,
                              2.02594437e-02f,
                              -2.59591341e-02f, 5.15986383e-02f, 5.39995618e-02f,
                              -9.85293090e-02f,
                              2.79084612e-02f, -1.11935120e-02f, -2.93335039e-02f,
                              -2.91536953e-02f,
                              -3.82210538e-02f, -2.37043500e-02f, -7.55588114e-02f,
                              5.86337373e-02f,
                              3.72418091e-02f, 5.03646545e-02f, 5.26626594e-02f,
                              -1.36754280e-02f,
                              7.66208116e-03f, -5.16738258e-02f, 4.43915278e-02f,
                              -1.23690637e-02f,
                              -1.71308517e-02f, -7.61585981e-02f, 9.61415917e-02f,
                              8.05187598e-02f,
                              -4.99857888e-02f, 6.97168857e-02f, -5.31303696e-02f,
                              -1.83348090e-03f,
                              -2.49218047e-02f, -6.51518926e-02f, -6.13980694e-03f,
                              2.01409981e-02f,
                              1.25074029e-01f, -9.15517733e-02f, -1.15703037e-02f,
                              1.03061441e-02f,
                              -6.43936098e-02f, -1.43284462e-02f, 1.25016517e-03f,
                              -1.39378076e-02f,
                              3.95851545e-02f, -9.25188605e-03f, -4.01583016e-02f,
                              4.65150224e-03f,
                              -1.48461957e-03f, -1.06359581e-02f, 1.98772573e-03f,
                              -4.31642756e-02f,
                              -3.80819328e-02f, 6.82118833e-02f, -6.70690015e-02f,
                              -9.01907030e-03f,
                              3.42186075e-03f, -5.22828698e-02f, -2.63943709e-02f,
                              1.29338528e-03f)
    }

    fun predict()
    {
        inferenceInterface.let {
            it.feed(INPUT_NODES[0], inputX, INPUT_DIM[0], INPUT_DIM[1])
            it.run(OUTPUT_NODES)

            it.fetch(OUTPUT_NODES[0], outputYPred)

            Log.d("OUTPUT", "Predict: ${outputYPred[0]}")
        }
    }

}


package com.example.taxi_full.view.home.user.bottom;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.model.RootOrderOne;
import com.example.taxi_full.view.home.user.HomeActivity;
import com.example.taxi_full.view.home.user.core.API.CoreAPI;
import com.yandex.mapkit.map.PlacemarkMapObject;

import org.java_websocket.client.WebSocketClient;

public interface BottomSheet {

    CoreAPI core = new CoreAPI();
    DBClass DBClass = new DBClass();


    /**
     * @param start   EditText
     * @param finish  EditText
     * @param context Context
     *                <p>
     *                API
     *                <p>
     *                Метод, которой блокирует внесение изменений в случае,
     *                если человек заказал такси
     */
    void EditTextLocked(EditText start, EditText finish, Context context);

    /**
     * @param home     EditText
     * @param work     EditText
     * @param context  Context
     * @param activity Activity
     *                 <p>
     *                 API
     *                 <p>
     *                 Медод подгружает данные в поля
     *                 адрес работы и адрес дома
     */
    void loadHomeWorkText(EditText home, EditText work, Context context, HomeActivity activity);

    /**
     * @param context  Context
     * @param activity Activity
     * @param home     EditText
     * @param work     EditText
     *                 <p>
     *                 API
     *                 <p>
     *                 Добавляет в БД данные об адрессе дома и работы, а так-же
     *                 сохраняет строки в EditText работы или дома
     */
    void addHomeWorkBottom(Context context, HomeActivity activity, EditText home, EditText work);

    /**
     * @param context Context
     * @return int
     * <p>
     * API
     * <p>
     * Если поля работы или дома нет в базе данных
     * метод возвращает 0, иначе - 1
     */
    int checkHomeWork(Context context);

    /**
     * @param rootOrderOne    RootOrderOne
     * @param activity        Activity
     * @param dollar_eco      ImageView
     * @param dollar_middle   ImageView
     * @param dollar_business ImageView
     *                        <p>
     *                        В зависимости от выбранного класса заказа замена
     *                        стандартного значка заказа на серый
     */
    void setImageDollarClass(RootOrderOne rootOrderOne, HomeActivity activity,
                             ImageView dollar_eco, ImageView dollar_middle, ImageView dollar_business);

    /**
     * @param activity   Activity
     * @param start      EditText
     * @param finish     EditText
     * @param eco        TextView
     * @param middle     TextView
     * @param business   TextView
     * @param typeNal    Button
     * @param typeOffNal Button
     *                   <p>
     *                   При нажатии кнопки заказать
     *                   блокируется возможность изменить параметры заказа
     */
    void blockEditOrder(HomeActivity activity,
                        EditText start, EditText finish,
                        TextView eco, TextView middle, TextView business,
                        Button typeNal, Button typeOffNal);

    /**
     * @param activity   Activity
     * @param start      EditText
     * @param finish     EditText
     * @param eco        TextView
     * @param middle     TextView
     * @param business   TextView
     * @param typeNal    Button
     * @param typeOffNal Button
     *                   <p>
     *                   При нажатии кнопки отменить
     *                   разблокируется возможность изменить параметры заказа
     */
    void unBlockEditOrder(HomeActivity activity,
                          EditText start, EditText finish,
                          TextView eco, TextView middle, TextView business,
                          Button typeNal, Button typeOffNal);


    /**
     * @param activity   Activity
     * @param context    Context
     * @param point1     Placemark
     * @param point2     Placemark
     * @param Class      int
     * @param start      EditText
     * @param finish     EditText
     * @param eco        TextView
     * @param middle     TextView
     * @param business   TextView
     * @param typeNal    Button
     * @param typeOffNal Button
     *                   <p>
     *                   API
     *                   <p>
     *                   Если активность заказа равна 2
     *                   высвечиваем кнопку отменить закз
     */
    void isStartOrder(HomeActivity activity, Context context,
                      PlacemarkMapObject point1, PlacemarkMapObject point2,
                      int Class,
                      EditText start, EditText finish,
                      TextView eco, TextView middle, TextView business,
                      Button typeNal, Button typeOffNal,
                      WebSocketClient mWebSocketClientButton);

    void bottomEditStartFinishPoint(HomeActivity activity, Context context, EditText start, EditText finish);

    void typeNalOffnal(HomeActivity activity, Context context,
                       Button typeNal, Button typeOffNal, int lightBlue);


    void start(EditText start, EditText finish, Context context,
               HomeActivity activity, EditText home, EditText work, PlacemarkMapObject point1, PlacemarkMapObject point2,
               int Class, TextView eco, TextView middle, TextView business,
               Button typeNal, Button typeOffNal,
               WebSocketClient mWebSocketClientButton,
               int lightBlue);
}

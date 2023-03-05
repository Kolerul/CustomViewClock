# Custom view в виде стрелочных часов

Проект содержит небольшое количество экранов, которые содержат различные варианты настройки созданного мною view в виде стрелочных часов.

В параметры настройки входят как обычые для всех view настройки параметров размеров и цветов, так и более уникальные возможности, например возможность заменить арабские цифры на римские, изменить стиль цифр и еще несколько настроек, для более комфортного восприятия.

Список возможных настроек:
- numberFont - стиль шрифта цифр
- backgroundColor - цвет заднего фона циферблата 
- borderColor - цвет границы часов
- hourArrowColor - цвет часовой стрелки
- minuteArrowColor - цвет минутной стрелки
- secondArrowColor - цвет секундной стрелки
- nailColor - цвет циферблата (цифр и секундных отсечек)
- romanNumbers - true - будут римские цифры, false - арабские
- numberRadiusCoef - коэффициент радиуса, на котором расположены цифры, относительно радиуса границы часов. Нужен в первую очередь для того, чтобы отодвинуть цифры, если из-за особенностей шрифта они начинают смешиваться с другими элементами циферблата
- centerCircleColor - цвет круга в центре часов, из которого исходят стрелки
- permanentSecondArrow - true - секундная стрелка будет двигаться плавно, false - секундная стрелка будет еремещаться раз в секунду
module com.kpouer.mapview {
    requires java.desktop;
    requires org.slf4j;
    requires com.kpouer.wkt;
    requires static lombok;
    requires static org.jetbrains.annotations;
    exports com.kpouer.mapview;
    exports com.kpouer.mapview.marker;
    exports com.kpouer.mapview.tile;
    exports com.kpouer.mapview.tile.cache;
}
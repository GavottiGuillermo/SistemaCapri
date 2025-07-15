package org.sistema;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.sistema.ModuloCargaArchivo.*;
import org.sistema.ModuloCashFlow.RegistroTransaccionCF;
import org.sistema.ModuloVentas.RegistroArticuloVentas;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public class Controlador_Pestanias implements Initializable {
    ////////////VARIABLES CASH FLOW ////////////////////////
    @FXML private TableView<RegistroTransaccionCF> tblCashFlow;
    @FXML private TableColumn<RegistroTransaccionCF, String> colFechaTransaccion;
    @FXML private TableColumn<RegistroTransaccionCF, String> colObservacion;
    @FXML private TableColumn<RegistroTransaccionCF, Double> colDebe;
    @FXML private TableColumn<RegistroTransaccionCF, Double> colHaber;
    @FXML private TableColumn<RegistroTransaccionCF, Double> colSaldo;
    @FXML private Array analisisArray;
    @FXML private DatePicker dtFechaInicialCF, dtFechaFinalCF;
    @FXML private Button btnConsultarCF;
    @FXML private Button btnLimpiarCF;
    @FXML private Label lbCantVentas;
    @FXML private Label lbTotalIngresos;
    @FXML private Label lbTotalGastos;
    @FXML private Label lbSaldoTotal;
    @FXML private Label lbMayorIngreso;
    @FXML private Label lbCantGasto;
    @FXML private Label lbDiaMayorIngreso;
    @FXML private Label lbDiaMayorGasto;
    private final ObservableList<RegistroTransaccionCF> lstObsCashflow = FXCollections.observableArrayList();

    ////////////VARIABLES VENTAS ////////////////////////
    @FXML private TableView<RegistroArticuloVentas> tblArticulosStock;
    @FXML private TableColumn<RegistroArticuloVentas, String> colIdArticuloVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colEstadoVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colNombreArticuloVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colCategoriaVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colColorVentas;
    @FXML private TableColumn<RegistroArticuloVentas, String> colTalleVentas;
    @FXML private TableColumn<RegistroArticuloVentas, Double> colPrecioEfectivoVentas;
    @FXML private TableColumn<RegistroArticuloVentas, Double> colPrecioTransferenciaVentas;
    @FXML private TableColumn<RegistroArticuloVentas, Void> colAccionesVentas;
    @FXML private TableView<RegistroArticuloVentas> tblArticuloEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colIdArticuloEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colNomArticuloEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colCategoriaEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colColorEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colTalleEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colPrecioEfectivoEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, String> colPrecioTransferenciaEstaCompra;
    @FXML private TableColumn<RegistroArticuloVentas, Void> colAccionesEstaCompra;
    @FXML private TextField fldBuscarNumArticulo;
    @FXML private TextField fldBuscarNomPrenda;
    @FXML private TextField fldNombreCliente;
    @FXML private TextField fldBuscarEstado;
    @FXML private TextField fldBuscarCategoria;
    String nombreCliente;
    String instagramCliente;
    String correoCliente;
    @FXML private TextField fldInstagramCliente;
    @FXML private TextField fldCorreoCliente;
    @FXML private Button btnRefrescarStock;
    @FXML private Label lbCantArticulos;
    @FXML private Label lbPrecioTotal;
    @FXML private Button btnFinalizarCompra;
    @FXML private Button btnCancelarCompra;
    @FXML private ComboBox cbMetodoPago;
    private final ObservableList<RegistroArticuloVentas> listaStock = FXCollections.observableArrayList();
    private FilteredList<RegistroArticuloVentas> listaFiltrada;
    private final ObservableList<RegistroArticuloVentas> listaCompra = FXCollections.observableArrayList();
    private double totalCompra = 0.0;
    ////////////VARIABLES CARGA ////////////////////////
    @FXML private Button btnBuscarArchivo;
    @FXML private Button btnCargarArchivo;
    @FXML private Label rutaArchivoCargado;
    @FXML private Button btnAgregarProducto;
    @FXML private Button btnFinalizarCarga;
    @FXML private TextField fldCostoLote,fldObservacion,fldInversor,fldNombreProductoLote,fldCategoriaProductoLote,
            fldColorProductoLote,fldTalleProductoLote,fldPrecioCompraProductoLote,fldPrecioVentaProductoLote;
    @FXML private TableView<RegistroProducto> tblProductosEsteLote;
    private ObservableList<RegistroProducto> lstCargaProductos = FXCollections.observableArrayList();
    @FXML private TableColumn<RegistroProducto, String> colNombreProdLote;
    @FXML private TableColumn<RegistroProducto, String> colCategoriaProdLote;
    @FXML private TableColumn<RegistroProducto, String> colColorProdLote;
    @FXML private TableColumn<RegistroProducto, String> colTalleProdLote;
    @FXML private TableColumn<RegistroProducto, Double> colPrecioCompraProdLote;
    @FXML private TableColumn<RegistroProducto, Double> colPrecioVentaProdLote;
    @FXML private File archivoSeleccionado;
    @FXML private List<RegistroLote> lstLotes;
    @FXML private List<RegistroProducto> lstProductos;
    @FXML private List<RegistroPago> lstPagos;
    @FXML private List<RegistroGastoExtra> lstGastosExtra;
    @FXML private List<RegistroCliente> lstClientes;
    @FXML Connection conexionABBDD;

    ////////////PESTAÑA CONSULTAR/MODIFICAR LISTAS ////////////////////////
    @FXML
    private ComboBox<String> cmbListas;
    @FXML
    private String listaActual = null;

    private Button btnRefrescarModifLista;

    @FXML
    private TableView<ObservableList<String>> tblConsulta;
    @FXML
    private TableColumn<ObservableList<String>, String> colGenerica; // Se crean dinámicamente

    private final String[] opcionesListas = {"productos", "lotes", "pagos", "gastos_extra", "clientes"};

    @Override public void initialize(URL location, ResourceBundle resources) {
        ////////////PESTAÑA VENTAS ////////////////////////
        btnFinalizarCompra.setDisable(true);
        btnCancelarCompra.setDisable(true);
        cargarDatosDesdeBBDD();
        configurarTablaStock();
        configurarTablaCompra();
        actualizarResumen();
        inicializarFiltrado();
        btnCancelarCompra.setOnAction(event -> {
            cancelarCompra();
        });
        btnFinalizarCompra.setOnAction(event -> {
            finalizarCompra();
        });

        ////////////PESTAÑA CASHFLOW ////////////////////////
        configurarTablaCashFlow();

        ////////////PESTAÑA CARGA ////////////////////////
        btnCargarArchivo.setDisable(true);
        rutaArchivoCargado.setText("");
        btnBuscarArchivo.setOnAction(event -> buscarArchivo());
        btnCargarArchivo.setOnAction(event -> {
            try {
                cargarArchivo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        configurarCargaLotes();

        ////////////PESTAÑA CONSULTAR/MODIFICAR LISTAS ////////////////////////
        cmbListas.getItems().addAll(opcionesListas);
        cmbListas.setValue("productos"); // Predeterminado
        cmbListas.setOnAction(event -> {
            try {
                cargarLista(cmbListas.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            cargarLista("productos"); // Carga inicial
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ////////////PESTAÑA CONSULTAR/MODIFICAR LISTAS ////////////////////////
    private void cargarLista(String nombreLista) throws SQLException {
        listaActual = nombreLista; // Guardamos la lista actual
        conectarABBDD();
        if (conexionABBDD != null) {
            String sql = "SELECT * FROM sp_consultar_lista(?)";
            PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
            stmt.setString(1, nombreLista);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Array colNamesArray = rs.getArray("col_names");
                String[] columnas = colNamesArray != null ? (String[]) colNamesArray.getArray() : new String[0];

                String registrosJson = rs.getString("registros");
                JSONArray registros = (registrosJson != null) ? new JSONArray(registrosJson) : new JSONArray();

                actualizarTabla(columnas, registros);
            } else {
                actualizarTabla(new String[0], new JSONArray());
            }
        }
    }

    private void actualizarTabla(String[] columnas, JSONArray registros) {
        tblConsulta.getColumns().clear(); // Borra columnas anteriores
        tblConsulta.getItems().clear();   // Borra datos anteriores

        // Crear las columnas respetando el orden original de 'columnas'
        for (int i = 0; i < columnas.length; i++) {
            final int index = i;
            String nombreOriginal = columnas[i];
            String nombreTransformado = nombreOriginal.replace("_", " ").toUpperCase();

            TableColumn<ObservableList<String>, String> col = new TableColumn<>(nombreTransformado);
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue().get(index))
            );
            tblConsulta.getColumns().add(col);
        }

        // Cargar los datos en el mismo orden que las columnas
        ObservableList<ObservableList<String>> datos = FXCollections.observableArrayList();
        for (int i = 0; i < registros.length(); i++) {
            JSONObject obj = registros.getJSONObject(i);
            ObservableList<String> fila = FXCollections.observableArrayList();

            for (String columna : columnas) {
                fila.add(obj.optString(columna, ""));
            }
            datos.add(fila);
        }
        tblConsulta.setItems(datos);

        // Agregar columna de Acciones
        TableColumn<ObservableList<String>, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnModificar, btnEliminar);

            {
                btnModificar.setOnAction(event -> {
                    ObservableList<String> fila = getTableView().getItems().get(getIndex());
                    mostrarVentanaModificacion(columnas, fila,tblConsulta);
                });

                btnEliminar.setOnAction(event -> {
                    ObservableList<String> fila = getTableView().getItems().get(getIndex());
                    mostrarVentanaEliminar(List.of(columnas), fila,tblConsulta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
        tblConsulta.getColumns().add(colAcciones);
    }

    public void mostrarVentanaModificacion(String[] columnas, ObservableList<String> fila,TableView<ObservableList<String>> tabla) {
        Stage ventana = new Stage();
        ventana.setTitle("Modificar Registro");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Map<String, TextField> camposTexto = new LinkedHashMap<>();

        for (int i = 0; i < columnas.length; i++) {
            String columna = columnas[i];
            TextField campo = new TextField(fila.get(i));
            campo.setPromptText(columna.replace("_", " ").toUpperCase());
            if (columna.toLowerCase().startsWith("id_")) {
                campo.setDisable(true); // 🔒 Deshabilita el campo si es ID
            }
            camposTexto.put(columna, campo);
            grid.add(new Label(columna.replace("_", " ").toUpperCase()), 0, i);
            grid.add(campo, 1, i);
        }


        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        Button btnGuardar = new Button("Guardar Cambios");
        Button btnEliminar = new Button("Eliminar Registro");
        Button btnCancelar = new Button("Cancelar");

        botones.getChildren().addAll(btnGuardar, btnEliminar, btnCancelar);
        grid.add(botones, 0, columnas.length, 2, 1);

        btnGuardar.setOnAction(e -> {
            try {
                Map<String, String> datos = new HashMap<>();
                for (Map.Entry<String, TextField> entry : camposTexto.entrySet()) {
                    datos.put(entry.getKey(), entry.getValue().getText());
                }

                // Validar si hay un campo ID
                Optional<String> campoID = datos.keySet().stream().filter(k -> k.toLowerCase().startsWith("id_")).findFirst();
                if (!campoID.isPresent()) {
                    mostrarAlerta("Error al guardar cambios", "No se encontró el campo ID en los datos.", Alert.AlertType.ERROR);
                    return;
                }
                ejecutarModificacionEnBBDD(datos);
                mostrarAlerta("Actualización", "Se actualizó el registro correctamente", Alert.AlertType.INFORMATION);
                String seleccionActual = cmbListas.getValue();
                // 🟡 Actualizar visualmente la fila modificada en la tabla
                for (int i = 0; i < columnas.length; i++) {
                    fila.set(i, camposTexto.get(columnas[i]).getText());
                }
                ventana.close();
                tabla.refresh();
            } catch (Exception ex) {
                mostrarAlerta("Error al guardar", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnEliminar.setOnAction(e -> {
            try {
                Map<String, String> datos = new HashMap<>();
                for (Map.Entry<String, TextField> entry : camposTexto.entrySet()) {
                    datos.put(entry.getKey(), entry.getValue().getText());
                }

                Optional<String> campoID = datos.keySet().stream().filter(k -> k.toLowerCase().startsWith("id_")).findFirst();
                if (!campoID.isPresent()) {
                    mostrarAlerta("Error al eliminar", "No se encontró el campo ID en los datos.", Alert.AlertType.ERROR);
                    return;
                }
                ejecutarEliminacionEnBBDD(datos);
                mostrarAlerta("Eliminación", "Se eliminó el registro correctamente", Alert.AlertType.INFORMATION);
                String seleccionActual = cmbListas.getValue();
                // 🟡 Actualizar visualmente la fila modificada en la tabla
                for (int i = 0; i < columnas.length; i++) {
                    fila.set(i, camposTexto.get(columnas[i]).getText());
                }
                ventana.close();
                tabla.refresh();

            } catch (Exception ex) {
                mostrarAlerta("Error al eliminar", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnCancelar.setOnAction(e -> ventana.close());

        Scene scene = new Scene(grid);
        ventana.setScene(scene);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.showAndWait();
    }

    public void mostrarVentanaEliminar(List<String> columnas, List<String> fila,TableView<ObservableList<String>> tabla) {
        Stage ventana = new Stage();
        ventana.setTitle("Confirmar Eliminación");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label mensaje = new Label("¿Estás seguro de eliminar este registro?");
        layout.getChildren().add(mensaje);

        Map<String, String> datos = new HashMap<>();
        for (int i = 0; i < columnas.size(); i++) {
            String campo = columnas.get(i);
            String valor = fila.get(i);
            datos.put(campo, valor);
            layout.getChildren().add(new Label(campo.replace("_", " ") + ": " + valor));
        }

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);
        Button btnConfirmar = new Button("Eliminar");
        Button btnCancelar = new Button("Cancelar");

        botones.getChildren().addAll(btnCancelar, btnConfirmar);
        layout.getChildren().add(botones);

        btnCancelar.setOnAction(e -> ventana.close());

        btnConfirmar.setOnAction(e -> {
            try {
                ejecutarEliminacionEnBBDD(datos);
                ventana.close();
                refrescarTablaActual();
                mostrarAlerta("Eliminación", "Se eliminó el registro correctamente", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error al eliminar","Error al eliminar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Scene escena = new Scene(layout);
        ventana.setScene(escena);
        ventana.initModality(Modality.APPLICATION_MODAL);
        ventana.showAndWait();
    }

    private void ejecutarModificacionEnBBDD(Map<String, String> datos) throws SQLException {
        conectarABBDD();
        if (conexionABBDD != null) {
            String nombreTabla = cmbListas.getValue().toLowerCase();

            // Se asume que el ID está siempre con nombre 'id_X', ejemplo: id_producto, id_cliente, etc.
            String nombreId = datos.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith("id_"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró el campo ID en los datos."));

            int valorId = Integer.parseInt(datos.get(nombreId));

            // Quitar el campo ID del JSON para actualizar solo los otros campos
            Map<String, String> camposSinId = new HashMap<>(datos);
            camposSinId.remove(nombreId);
            String sql = "CALL public.sp_modificar_registro(?, ?, ?, ?)";
            PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
            stmt.setString(1, nombreTabla);
            stmt.setString(2, nombreId);
            stmt.setInt(3, valorId);
            PGobject jsonObject = new PGobject();
            jsonObject.setType("json");
            jsonObject.setValue(new JSONObject(camposSinId).toString());
            stmt.setObject(4, jsonObject);

            stmt.execute();
        }
        desconexionABBDD();
    }

    private void ejecutarEliminacionEnBBDD(Map<String, String> datos) throws SQLException {
        conectarABBDD();
        if (conexionABBDD != null) {
            String nombreTabla = cmbListas.getValue();

            String nombreId = datos.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith("id_"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No se encontró el campo ID en los datos."));

            int valorId = Integer.parseInt(datos.get(nombreId));
            String sql = "call sp_eliminar_registro(?, ?, ?) ";
            PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
            stmt.setString(1, nombreTabla);
            stmt.setString(2, nombreId);
            stmt.setInt(3, valorId);

            stmt.execute();
        }
        desconexionABBDD();
    }

    private void refrescarTablaActual() {
        if (listaActual != null) {
            try {
                cargarLista(listaActual);
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error al refrescar la tabla", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }



    private String toCamelCase(String text) {
        String[] parts = text.split("_");
        StringBuilder camelCase = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                camelCase.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase()).append(" ");
            }
        }
        return camelCase.toString().trim();
    }


    //////////// FUNCIONES PESTAÑA CARGA ////////////////////////
    private void buscarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));

        // Abre el cuadro de diálogo y almacena el archivo seleccionado
        archivoSeleccionado = fileChooser.showOpenDialog(new Stage());

        if (archivoSeleccionado != null) {
            // Muestra la ruta del archivo en la etiqueta y habilita el botón de carga
            rutaArchivoCargado.setText(archivoSeleccionado.getAbsolutePath());
            btnCargarArchivo.setDisable(false);
        }
    }

    private void cargarArchivo() throws IOException {
        if (archivoSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación de Carga");
            confirmacion.setHeaderText("Se cargará archivo seleccionado");
            confirmacion.setContentText("¿Seguro desea continuar?");
            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                System.out.println("Cargando el archivo: " + archivoSeleccionado.getAbsolutePath());
                cargarExcelABBDD(archivoSeleccionado);

                initialize(null, null);  // Reinicia el controlador o realiza otra acción al finalizar la carga
            } else {
                // Si el usuario cancela, no se realiza ninguna acción
                System.out.println("Carga de archivo cancelada por el usuario.");
            }
        }
    }

    private void cargarExcelABBDD(File in_ArchivoSeleccionado) throws IOException {
        lstLotes = new ArrayList<>();
        lstProductos = new ArrayList<>();
        lstPagos = new ArrayList<>();
        lstGastosExtra = new ArrayList<>();
        lstClientes = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(in_ArchivoSeleccionado);
            Workbook workbook = new XSSFWorkbook(fis)) {
            //--------------------
            System.out.println("Estoy procesando >>Lotes<<");
            Sheet hojaLotes = workbook.getSheet("Lotes");
            cargarListaLotes(hojaLotes);
            conectarABBDD();
            cargarListaLotesABBDD(lstLotes);
            desconexionABBDD();
            //-----------------------
            System.out.println("Estoy procesando >>Productos<<");
            Sheet hojaProductos = workbook.getSheet("Productos");
            cargarListaProductos(hojaProductos);
            conectarABBDD();
            cargarListaProductosABBDD(lstProductos);
            desconexionABBDD();
            //-----------------------
            System.out.println("Estoy procesando >>Pagos<<");
            Sheet hojaPagos = workbook.getSheet("Pagos");
            cargarListaPagos(hojaPagos);
            conectarABBDD();
            cargarListaPagosABBDD(lstPagos);
            desconexionABBDD();
            //-----------------------
            System.out.println("Estoy procesando >>Gastos Extra<<");
            Sheet hojaGastosExtra = workbook.getSheet("Gastos Extra");
            cargarListaGastosExtra(hojaGastosExtra);
            conectarABBDD();
            cargarListaGastosExtraABBDD(lstGastosExtra);
            desconexionABBDD();
            //-----------------------
            System.out.println("Estoy procesando >>Clientes<<");
            Sheet hojaClientes = workbook.getSheet("Clientes");
            cargarListaClientes(hojaClientes);
            conectarABBDD();
            cargarListaClientesABBDD(lstClientes);
            desconexionABBDD();
            mostrarAlerta("Carga Masiva", "Archivo cargado correctamente a Base de Datos", Alert.AlertType.INFORMATION);
            resetearCargaArchivo();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void resetearCargaArchivo() {
        btnBuscarArchivo.setDisable(false);
        rutaArchivoCargado.setText("");
        btnCargarArchivo.setDisable(true);
    }

    private void cargarListaLotes(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                Cell celdaIdLote = fila.getCell(0);
                Cell celdaFecha = fila.getCell(1);
                Cell celdaCosto = fila.getCell(2);
                Cell celdaLocal = fila.getCell(3);
                Cell celdaInversor = fila.getCell(4);

                RegistroLote registroLote = new RegistroLote((int) celdaIdLote.getNumericCellValue(), celdaFecha.getStringCellValue(),
                        celdaCosto.getNumericCellValue(), celdaLocal.getStringCellValue(), celdaInversor.getStringCellValue());

                registroLote.setId_lote((int) celdaIdLote.getNumericCellValue());
                registroLote.setFecha(celdaFecha.getStringCellValue());
                registroLote.setCosto(celdaCosto.getNumericCellValue());
                registroLote.setLocal(celdaLocal.getStringCellValue());
                registroLote.setInversor(celdaInversor.getStringCellValue());

                lstLotes.add(registroLote);
            }
        }
    }

    private void cargarListaProductos(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                Cell celdaIdArticulo = fila.getCell(0);
                if (celdaIdArticulo != null){
                    Cell celdaEstado = fila.getCell(1);
                    Cell celdaNombrePrenda = fila.getCell(2);
                    Cell celdaCategoria = fila.getCell(3);
                    Cell celdaColor = fila.getCell(4);
                    Cell celdaTalle = fila.getCell(5);
                    Cell celdaPrecioCompra = fila.getCell(6);
                    Cell celdaPrecioVentaEfectivo = fila.getCell(7);
                    Cell celdaPrecioVentaTransf = fila.getCell(8);
                    Cell celdaId_Lote = fila.getCell(9);

                    System.out.println(celdaEstado.getStringCellValue());
                    System.out.println(celdaNombrePrenda.getStringCellValue());
                    System.out.println(celdaCategoria.getStringCellValue());
                    System.out.println(celdaColor.getStringCellValue());
                    System.out.println(celdaTalle.getStringCellValue());
                    System.out.println(celdaPrecioCompra.getNumericCellValue());
                    System.out.println(celdaPrecioVentaEfectivo.getNumericCellValue());
                    System.out.println(celdaPrecioVentaTransf.getNumericCellValue());
                    System.out.println((int)celdaId_Lote.getNumericCellValue());

                    RegistroProducto registroProducto = new RegistroProducto(celdaEstado.getStringCellValue(),
                            celdaNombrePrenda.toString(), celdaCategoria.toString(), celdaColor.toString()
                            , celdaTalle.toString(), celdaPrecioCompra.getColumnIndex(), celdaPrecioVentaEfectivo.getNumericCellValue(),
                            celdaPrecioVentaTransf.getNumericCellValue(), (int) celdaId_Lote.getNumericCellValue());

                    registroProducto.setEstado(celdaEstado.getStringCellValue());
                    registroProducto.setNombrePrenda(celdaNombrePrenda.getStringCellValue());
                    registroProducto.setCategoria(celdaCategoria.getStringCellValue());
                    registroProducto.setColor(celdaColor.getStringCellValue());
                    registroProducto.setTalle(celdaTalle.getStringCellValue());
                    registroProducto.setPrecioCompra(celdaPrecioCompra.getNumericCellValue());
                    registroProducto.setPrecioVentaEfectivo(celdaPrecioVentaEfectivo.getNumericCellValue());
                    registroProducto.setPrecioVentaTransf(celdaPrecioVentaTransf.getNumericCellValue());
                    System.out.println("-----------------------------------------");

                    lstProductos.add(registroProducto);
                }

            }
        }
    }

    private void cargarListaPagos(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                Cell celdaFecha = fila.getCell(0);
                Cell celdaMonto = fila.getCell(1);
                Cell celdaNombreCliente = fila.getCell(2);
                Cell celdaMetodoPago = fila.getCell(3);
                RegistroPago registroPago = new RegistroPago(celdaFecha.getStringCellValue(), celdaMonto.getNumericCellValue(), celdaNombreCliente.toString()
                        , celdaMetodoPago.toString());
                registroPago.setFecha(celdaFecha.getStringCellValue());
                registroPago.setMonto(celdaMonto.getNumericCellValue());
                registroPago.setNombreCliente(celdaNombreCliente.getStringCellValue());
                registroPago.setMetodoPago(celdaMetodoPago.getStringCellValue());
                lstPagos.add(registroPago);
            }
        }
    }

    private void cargarListaGastosExtra(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                Cell celdaFecha = fila.getCell(0);
                Cell celdaCosto = fila.getCell(1);
                Cell celdaInversor = fila.getCell(2);
                Cell celdaObservacion = fila.getCell(3);
                RegistroGastoExtra registroGastoExtra = new RegistroGastoExtra(celdaFecha.getStringCellValue(), celdaCosto.getNumericCellValue(),
                        celdaInversor.getStringCellValue(), celdaObservacion.getStringCellValue());
                registroGastoExtra.setFecha(celdaFecha.getStringCellValue());
                registroGastoExtra.setMonto(celdaCosto.getNumericCellValue());
                registroGastoExtra.setInversor(celdaInversor.getStringCellValue());
                registroGastoExtra.setObservacion(celdaObservacion.getStringCellValue());
                lstGastosExtra.add(registroGastoExtra);
            }
        }
    }

    private void cargarListaClientes(Sheet hojaXLS) {
        Row filaPrimerDato = hojaXLS.getRow(1);
        Cell celdaPrimerDato = filaPrimerDato.getCell(0);
        if (celdaPrimerDato != null) {
            for (int i = 1; i <= hojaXLS.getLastRowNum(); i++) {
                Row fila = hojaXLS.getRow(i);
                Cell celdaNombre = fila.getCell(0);
                Cell celdaCorreo = fila.getCell(1);
                Cell celdaInstagram = fila.getCell(2);
                RegistroCliente registroClientes = new RegistroCliente(celdaNombre.getStringCellValue(), celdaCorreo.getStringCellValue(),
                        celdaInstagram.getStringCellValue());
                registroClientes.setNombre(celdaNombre.getStringCellValue());
                registroClientes.setCorreo(celdaCorreo.getStringCellValue());
                registroClientes.setInstagram(celdaInstagram.getStringCellValue());
                lstClientes.add(registroClientes);
            }
        }
    }

    private void cargarListaLotesABBDD(List<RegistroLote> listaLotes){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaLotes.size(); i++) {
                    String sql = "CALL public.sp_inserta_lotes(?, ?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setTimestamp(1, convertirStringATimestamp(listaLotes.get(i).getFecha()));
                    stmt.setDouble(2, listaLotes.get(i).getCosto());
                    stmt.setString(3, String.valueOf(listaLotes.get(i).getLocal()));
                    stmt.setString(4, String.valueOf(listaLotes.get(i).getInversor()));

                    System.out.println(convertirStringATimestamp(listaLotes.get(i).getFecha()));
                    System.out.println(listaLotes.get(i).getCosto());
                    System.out.println(String.valueOf(listaLotes.get(i).getLocal()));
                    System.out.println(String.valueOf(listaLotes.get(i).getInversor()));
                    System.out.println("-----------------------------");


                    stmt.execute();
                    System.out.println("Procedimiento almacenado ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cargarListaProductosABBDD(List<RegistroProducto> listaProductos){
        if (conexionABBDD != null) {
            try {
                for (int i = 0; i < listaProductos.size(); i++) {
                    String sql = "CALL public.sp_inserta_productos(?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setString(1, String.valueOf(listaProductos.get(i).getEstado()));
                    stmt.setString(2, String.valueOf(listaProductos.get(i).getNombrePrenda()));
                    stmt.setString(3, String.valueOf(listaProductos.get(i).getCategoria()));
                    stmt.setString(4, String.valueOf(listaProductos.get(i).getColor()));
                    stmt.setString(5, String.valueOf(listaProductos.get(i).getTalle()));
                    stmt.setDouble(6, listaProductos.get(i).getPrecioCompra());
                    stmt.setDouble(7, listaProductos.get(i).getPrecioVentaEfectivo());
                    stmt.setDouble(8, listaProductos.get(i).getPrecioVentaTransf());
                    stmt.setInt(9, listaProductos.get(i).getId_lote());
                    System.out.println("------------------Cargando a bd---------------------");
                    System.out.println(listaProductos.get(i).getEstado());
                    System.out.println(listaProductos.get(i).getNombrePrenda());
                    System.out.println(listaProductos.get(i).getCategoria());
                    System.out.println(listaProductos.get(i).getColor());
                    System.out.println(listaProductos.get(i).getTalle());
                    System.out.println(listaProductos.get(i).getEstado());
                    System.out.println(listaProductos.get(i).getPrecioVentaEfectivo());
                    System.out.println(listaProductos.get(i).getPrecioVentaTransf());
                    System.out.println("-----------------------------");
                    stmt.execute();
                    System.out.println("Procedimiento almacenado ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
            refrescarTablaStock();
        }
    }

    private void cargarListaPagosABBDD(List<RegistroPago> listaPagos){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaPagos.size(); i++) {
                    String sql = "CALL public.sp_inserta_pagos(?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setDouble(1, listaPagos.get(i).getMonto());
                    stmt.setString(2, String.valueOf(listaPagos.get(i).getNombreCliente()));
                    stmt.setString(3, String.valueOf(listaPagos.get(i).getMetodoPago()));

                    System.out.println(listaPagos.get(i).getMonto());
                    System.out.println(String.valueOf(listaPagos.get(i).getNombreCliente()));
                    System.out.println(String.valueOf(listaPagos.get(i).getMetodoPago()));
                    System.out.println("-----------------------------");


                    stmt.execute();
                    System.out.println("Procedimiento almacenado "+ sql + " ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cargarListaGastosExtraABBDD(List<RegistroGastoExtra> listaGastoExtra){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaGastoExtra.size(); i++) {
                    String sql = "CALL public.sp_inserta_gasto_extra(?, ?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setTimestamp(1, convertirStringATimestamp(listaGastoExtra.get(i).getFecha()));
                    stmt.setDouble(2, listaGastoExtra.get(i).getMonto());
                    stmt.setString(3, String.valueOf(listaGastoExtra.get(i).getInversor()));
                    stmt.setString(4, String.valueOf(listaGastoExtra.get(i).getObservacion()));

                    System.out.println(convertirStringATimestamp(listaGastoExtra.get(i).getFecha()));
                    System.out.println(listaGastoExtra.get(i).getMonto());
                    System.out.println(String.valueOf(listaGastoExtra.get(i).getInversor()));
                    System.out.println(String.valueOf(listaGastoExtra.get(i).getObservacion()));
                    System.out.println("-----------------------------");

                    stmt.execute();
                    System.out.println("Procedimiento almacenado "+ sql + " ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void cargarListaClientesABBDD(List<RegistroCliente> listaClientes){
        if (conexionABBDD != null) {
            try {

                for (int i = 0; i < listaClientes.size(); i++) {
                    String sql = "CALL public.sp_inserta_clientes(?, ?, ?)";
                    PreparedStatement stmt = conexionABBDD.prepareStatement(sql);
                    stmt.setString(1, listaClientes.get(i).getNombre());
                    stmt.setString(2, listaClientes.get(i).getCorreo());
                    stmt.setString(3, listaClientes.get(i).getInstagram());

                    System.out.println(listaClientes.get(i).getNombre());
                    System.out.println(listaClientes.get(i).getCorreo());
                    System.out.println(listaClientes.get(i).getInstagram());

                    System.out.println("-----------------------------");

                    stmt.execute();
                    System.out.println("Procedimiento almacenado "+ sql + " ejecutado correctamente.");

                    stmt.close();
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Timestamp convertirStringATimestamp(String fechaString) {
        try {
            // Define el formato de fecha esperado
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            // Convierte el String a un objeto java.util.Date
            java.util.Date fechaUtil = formatoFecha.parse(fechaString);
            // Convierte el objeto java.util.Date a java.sql.Timestamp
            return new Timestamp(fechaUtil.getTime());
        } catch (ParseException e) {
            // Manejo de errores si la fecha no tiene el formato esperado
            System.err.println("Error al parsear la fecha: " + e.getMessage());
            return null;
        }
    }

    /////// Carga MANUAL //////
    public void configurarCargaLotes() {
        // Deshabilitar botones al inicio
        btnFinalizarCarga.setDisable(true);

        // Validaciones de campos de producto
        lstCargaProductos.addListener((ListChangeListener<RegistroProducto>) change -> {
            btnFinalizarCarga.setDisable(lstCargaProductos.isEmpty());
        });

        colNombreProdLote.setCellValueFactory(new PropertyValueFactory<>("nombrePrenda"));
        colCategoriaProdLote.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colColorProdLote.setCellValueFactory(new PropertyValueFactory<>("color"));
        colTalleProdLote.setCellValueFactory(new PropertyValueFactory<>("talle"));
        colPrecioCompraProdLote.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colPrecioVentaProdLote.setCellValueFactory(new PropertyValueFactory<>("precioVentaTransf"));

        tblProductosEsteLote.setItems(lstCargaProductos);
    }

    private boolean validarCamposProducto() {
        String mensajeError = "";

        if (fldNombreProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Nombre del Producto' está vacío.\n";
        }
        if (fldCategoriaProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Categoría' está vacío.\n";
        }
        if (fldColorProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Color' está vacío.\n";
        }
        if (fldTalleProductoLote.getText().trim().isEmpty()) {
            mensajeError += "⚠ El campo 'Talle' está vacío.\n";
        }
        if (!fldPrecioCompraProductoLote.getText().matches("\\d+(\\.\\d+)?")) {
            mensajeError += "⚠ El campo 'Precio de Compra' debe ser un número válido.\n";
        }
        if (!fldPrecioVentaProductoLote.getText().matches("\\d+(\\.\\d+)?")) {
            mensajeError += "⚠ El campo 'Precio de Venta' debe ser un número válido.\n";
        }

        // Convertir los valores de precio a números
        double precioCompra = Double.parseDouble(fldPrecioCompraProductoLote.getText());
        double precioVenta = Double.parseDouble(fldPrecioVentaProductoLote.getText());

        // Validar que el precio de venta no sea menor al de compra
        if (precioVenta < precioCompra) {
            mensajeError += ("⚠ El 'Precio de Venta' no puede ser menor que el 'Precio de Compra'.\n");
        }

        // Si hay errores, mostrar alerta
        if (!mensajeError.isEmpty()) {
            mostrarAlerta("Campos Incorrectos", mensajeError, Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void cancelarCargaLote() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro de que desea cancelar la carga del lote?");
        confirmacion.setContentText("Si confirma, los datos ingresados serán eliminados y se reiniciará el formulario.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            limpiarFormulario();
            mostrarAlerta("Carga Cancelada", "Se ha cancelado la carga del lote y se ha reiniciado el formulario.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void agregarProducto() {
        if (!validarCamposProducto()) {
            return;
        }

        // 10% descuento
        RegistroProducto nuevoProducto = new RegistroProducto(
                "Disponible",
                fldNombreProductoLote.getText(),
                fldCategoriaProductoLote.getText(),
                fldColorProductoLote.getText(),
                fldTalleProductoLote.getText(),
                Double.parseDouble(fldPrecioCompraProductoLote.getText()),
                Double.parseDouble(fldPrecioVentaProductoLote.getText()) * 0.9, // 10% descuento
                Double.parseDouble(fldPrecioVentaProductoLote.getText()),
                0
        );

        lstCargaProductos.add(nuevoProducto);
        tblProductosEsteLote.refresh();
        btnFinalizarCarga.setDisable(lstCargaProductos.isEmpty());
        limpiarCamposProducto();
        System.out.println(lstCargaProductos);
    }

    private void insertarLoteYProductos() throws SQLException {

        JSONArray productosJson = new JSONArray();
        double costoLote = 0;

        for (RegistroProducto producto : lstCargaProductos) {
            costoLote += producto.getPrecioCompra();
            JSONObject obj = new JSONObject();
            obj.put("estado", "Disponible");
            obj.put("prenda", producto.getNombrePrenda());
            obj.put("categoria", producto.getCategoria());
            obj.put("color", producto.getColor());
            obj.put("talle", producto.getTalle());
            obj.put("precio_compra", producto.getPrecioCompra());
            obj.put("precio_venta_efectivo", producto.getPrecioVentaEfectivo());
            obj.put("precio_venta_transferencia", producto.getPrecioVentaTransf());

            productosJson.put(obj);
        }

        // Convertir el JSONArray a String
        String productosJsonString = productosJson.toString();

        conectarABBDD();
        PreparedStatement stmt = null;

        try {
            conexionABBDD.setAutoCommit(false);
            stmt = conexionABBDD.prepareStatement("CALL sp_inserta_lote_y_productos(?, ?, ?, ?)");
            stmt.setDouble(1, costoLote);
            stmt.setString(2, fldObservacion.getText());
            stmt.setString(3, fldInversor.getText());
            stmt.setString(4, productosJsonString); // Enviar JSON como String

            try {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error en la carga", "No se pudo insertar el lote y productos: " + e.getMessage(), Alert.AlertType.ERROR);
            }
            conexionABBDD.commit();
            System.out.println("Lote y productos insertados correctamente.");
        } finally {
            if (stmt != null) stmt.close();
            desconexionABBDD();
        }
    }


    @FXML
    private void finalizarCarga() throws SQLException {
        // Validar que los campos obligatorios no estén vacíos
        if (fldObservacion.getText().trim().isEmpty() || fldInversor.getText().trim().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Debes completar los campos de Observación e Inversor para finalizar la carga.", Alert.AlertType.ERROR);
            return;
        }
        // Mostrar cuadro de confirmación antes de proceder
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Carga");
        confirmacion.setHeaderText("¿Está seguro de que desea finalizar la carga del lote?");
        confirmacion.setContentText("Esta acción insertará el lote y los productos en la base de datos.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Insertar lote y productos en la base de datos
                insertarLoteYProductos();

                // Mostrar mensaje de éxito
                mostrarAlerta("Éxito", "Lote y productos cargados correctamente.", Alert.AlertType.INFORMATION);

                // Limpiar formulario después de la carga exitosa
                limpiarFormulario();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar el lote: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Usuario canceló la operación
            mostrarAlerta("Cancelado", "La carga del lote ha sido cancelada.", Alert.AlertType.INFORMATION);
        }
    }

    private void limpiarCamposProducto() {
        fldNombreProductoLote.clear();
        fldCategoriaProductoLote.clear();
        fldColorProductoLote.clear();
        fldTalleProductoLote.clear();
        fldPrecioCompraProductoLote.clear();
        fldPrecioVentaProductoLote.clear();
    }

    private void limpiarFormulario() {
        fldObservacion.clear();
        fldInversor.clear();
        lstCargaProductos.clear();

        btnAgregarProducto.setDisable(true);
        btnFinalizarCarga.setDisable(true);

    }

    //////////// FUNCIONES PESTAÑA VENTAS ////////////////////////
    private void configurarTablaStock() {
        colIdArticuloVentas.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colEstadoVentas.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstadoVentas.setCellFactory(column -> new TableCell<RegistroArticuloVentas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);

                    if (item.equals("Disponible")) {
                        setTextFill(javafx.scene.paint.Color.GREEN);
                        setStyle("-fx-text-fill: black; -fx-background-color: lightgreen;");
                    } else if (item.equals("Sin stock") || item.equals("Sin Stock")) {
                        setTextFill(javafx.scene.paint.Color.RED);
                        setStyle("-fx-text-fill: black; -fx-background-color: lightcoral;");
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK);
                        setStyle(""); // Restablecer estilo para otros casos
                    }
                }
            }
        });
        colNombreArticuloVentas.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        colCategoriaVentas.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colColorVentas.setCellValueFactory(new PropertyValueFactory<>("color"));
        colTalleVentas.setCellValueFactory(new PropertyValueFactory<>("talle"));
        colPrecioEfectivoVentas.setCellValueFactory(new PropertyValueFactory<>("precioEfectivo"));
        colPrecioTransferenciaVentas.setCellValueFactory(new PropertyValueFactory<>("precioTransferencia"));
        colAccionesVentas.setCellFactory(column -> new TableCell<>() {
            private final Button btnAgregar = new Button("Agregar al carrito");
            private final Button btnDevolver = new Button("Devolución");

            {
                // Acción del botón Agregar al carrito
                btnAgregar.setOnAction(event -> {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());
                    agregarAlCarrito(articulo);
                });

                // Acción del botón Devolución
                btnDevolver.setOnAction(event -> {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmar devolución");
                    alert.setHeaderText("¿Desea realizar la devolución del artículo?");
                    alert.setContentText(
                            "ID: " + articulo.getIdArticulo() + "\n" +
                                    "Nombre: " + articulo.getNombreArticulo() + "\n" +
                                    "Talle: " + articulo.getTalle() + "\n" +
                                    "Color: " + articulo.getColor() + "\n" +
                                    "Precio Transferencia: " + articulo.getPrecioTransferencia()
                    );

                    ButtonType btnConfirmar = new ButtonType("Confirmar");
                    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(btnConfirmar, btnCancelar);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == btnConfirmar) {
                        devolverArticulo(articulo);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());
                    if ("Disponible".equalsIgnoreCase(articulo.getEstado())) {
                        setGraphic(btnAgregar);
                    } else {
                        setGraphic(btnDevolver);
                    }
                }
            }
        });

        listaFiltrada = new FilteredList<>(listaStock, p -> true);
        tblArticulosStock.setItems(listaFiltrada);
        btnRefrescarStock.setOnAction(event -> refrescarTablaStock());
    }

    @FXML
    private void inicializarFiltrado() {
        fldBuscarNumArticulo.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
        fldBuscarNomPrenda.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
        fldBuscarEstado.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
        fldBuscarCategoria.textProperty().addListener((observable, oldValue, newValue) -> filtrarVentas());
    }

    private void filtrarVentas() {
        String filtroNumArticulo = fldBuscarNumArticulo.getText().trim();
        String filtroNombrePrenda = fldBuscarNomPrenda.getText().trim().toLowerCase();
        String filtroEstado = fldBuscarEstado.getText().trim().toLowerCase();
        String filtroCategoria = fldBuscarCategoria.getText().trim().toLowerCase();

        // Filtrar listaStock según los criterios de búsqueda
        ObservableList<RegistroArticuloVentas> listaFiltrada = FXCollections.observableArrayList();

        for (RegistroArticuloVentas articulo : listaStock) {
            boolean coincideNumArticulo = filtroNumArticulo.isEmpty() ||
                    String.valueOf(articulo.getIdArticulo()).contains(filtroNumArticulo);
            boolean coincideNombrePrenda = filtroNombrePrenda.isEmpty() ||
                    articulo.getNombreArticulo().toLowerCase().contains(filtroNombrePrenda);
            boolean coincideEstado = filtroEstado.isEmpty() ||
                    (articulo.getEstado() != null && articulo.getEstado().toLowerCase().contains(filtroEstado));
            boolean coincideCategoria = filtroCategoria.isEmpty() ||
                    (articulo.getCategoria() != null && articulo.getCategoria().toLowerCase().contains(filtroCategoria));

            if (coincideNumArticulo && coincideNombrePrenda && coincideEstado && coincideCategoria) {
                listaFiltrada.add(articulo);
            }
        }

        // Actualizar la tabla con la lista filtrada
        tblArticulosStock.setItems(listaFiltrada);
    }

    private void configurarTablaCompra() {
        colIdArticuloEstaCompra.setCellValueFactory(new PropertyValueFactory<>("idArticulo"));
        colNomArticuloEstaCompra.setCellValueFactory(new PropertyValueFactory<>("nombreArticulo"));
        colCategoriaEstaCompra.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colColorEstaCompra.setCellValueFactory(new PropertyValueFactory<>("color"));
        colTalleEstaCompra.setCellValueFactory(new PropertyValueFactory<>("talle"));
        colPrecioEfectivoEstaCompra.setCellValueFactory(new PropertyValueFactory<>("precioEfectivo"));
        colPrecioTransferenciaEstaCompra.setCellValueFactory(new PropertyValueFactory<>("precioTransferencia"));

        // Configurar columna de acciones
        colAccionesEstaCompra.setCellFactory(column -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEliminar.setOnAction(event -> {
                    RegistroArticuloVentas articulo = getTableView().getItems().get(getIndex());
                    eliminarDelCarrito(articulo);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
        tblArticuloEstaCompra.setItems(listaCompra);
    }

    private void cargarDatosDesdeBBDD() {
        listaStock.clear();
        String query = "{CALL sp_consultar_listado_ventas()}";
        conectarABBDD();
        try (CallableStatement callableStatement = conexionABBDD.prepareCall(query)) {

            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                RegistroArticuloVentas articuloVentas = new RegistroArticuloVentas(
                        resultSet.getInt("out_id_articulo"),
                        resultSet.getString("out_estado"),
                        resultSet.getString("out_nombre_articulo"),
                        resultSet.getString("out_categoria"),
                        resultSet.getString("out_color"),
                        resultSet.getString("out_talle"),
                        resultSet.getDouble("out_precio_venta_efectivo"),
                        resultSet.getDouble("out_precio_venta_transferencia")
                );
                listaStock.add(articuloVentas);
            }
            desconexionABBDD();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar datos", "No se pudo obtener la información de la base de datos.", Alert.AlertType.ERROR);
        }
    }

    private void agregarAlCarrito(RegistroArticuloVentas articulo) {
        if (!listaCompra.contains(articulo)) {
            listaCompra.add(articulo);
            totalCompra += articulo.getPrecioTransferencia(); // Actualizar con precio efectivo
            actualizarResumen();
        } else {
            mostrarAlerta("Artículo duplicado", "El artículo ya está en la compra actual.", Alert.AlertType.ERROR);
        }
    }

    private void eliminarDelCarrito(RegistroArticuloVentas articulo) {
        listaCompra.remove(articulo);
        totalCompra -= articulo.getPrecioTransferencia();
        actualizarResumen();
        if(listaCompra.isEmpty()) {
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
        }
    }

    private void actualizarResumen() {
        lbCantArticulos.setText(String.valueOf(listaCompra.size()));
       /* if (cbMetodoPago.getValue() != null && cbMetodoPago.getValue().equals("Efectivo")) {
            totalCompra = totalCompra * 0.9; // Aplica el 10% de descuento
        }*/
        lbPrecioTotal.setText(totalCompra + " $");
        if(totalCompra>0){
            btnFinalizarCompra.setDisable(false);
            btnCancelarCompra.setDisable(false);
        }else{
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
        }
    }

    @FXML
    private void refrescarTablaStock() {
        // Limpiar los campos de búsqueda
        fldBuscarNumArticulo.clear();
        fldBuscarNomPrenda.clear();
        fldBuscarEstado.clear();
        fldBuscarCategoria.clear();

        // Recargar los datos y reconfigurar la tabla
        cargarDatosDesdeBBDD();
        configurarTablaStock();
    }
    @FXML
    private void cancelarCompra() {
        listaCompra.clear();
        totalCompra = 0.0;
        actualizarResumen();
        if(listaCompra.isEmpty()) {
            btnFinalizarCompra.setDisable(true);
            btnCancelarCompra.setDisable(true);
        }
    }
    @FXML
    private void finalizarCompra() {
        // Obtener datos del cliente (si están vacíos, establecer "No proporcionado")
        nombreCliente = fldNombreCliente.getText().trim().isEmpty() ? "No proporcionado" : fldNombreCliente.getText().trim();
        instagramCliente = fldInstagramCliente.getText().trim().isEmpty() ? "No proporcionado" : fldInstagramCliente.getText().trim();
        correoCliente = fldCorreoCliente.getText().trim().isEmpty() ? "No proporcionado" : fldCorreoCliente.getText().trim();

        // Crear los botones de opción para el método de pago
        RadioButton rbEfectivo = new RadioButton("Efectivo");
        RadioButton rbTransferencia = new RadioButton("Transferencia");
        rbTransferencia.setSelected(true); // Transferencia por defecto

        ToggleGroup grupoPago = new ToggleGroup();
        rbEfectivo.setToggleGroup(grupoPago);
        rbTransferencia.setToggleGroup(grupoPago);

        // Contenedor de botones de pago
        VBox vboxPago = new VBox(10, rbEfectivo, rbTransferencia);
        vboxPago.setAlignment(Pos.CENTER_LEFT);

        // Contenedor para el resumen
        VBox vboxResumen = new VBox(15, vboxPago);
        vboxResumen.setAlignment(Pos.CENTER);

        // Generar el resumen de la compra
        StringBuilder resumenCompra = new StringBuilder();
        resumenCompra.append("Cliente: ").append(nombreCliente).append("\n")
                .append("Instagram: ").append(instagramCliente).append("\n")
                .append("Correo: ").append(correoCliente).append("\n\n")
                .append("Detalle de compra:\n");

        double totalCompra = 0;

        for (RegistroArticuloVentas producto : listaCompra) {
            double precio = rbTransferencia.isSelected() ?
                    producto.getPrecioTransferencia() :
                    producto.getPrecioEfectivo();
            totalCompra += precio;
            resumenCompra.append(String.format("- %s: $%.2f\n", producto.getNombreArticulo(), precio));
        }

        // Aplicar descuento si se selecciona efectivo
        double finalTotalCompra = totalCompra;
        rbEfectivo.setOnAction(event -> {
            double totalConDescuento = finalTotalCompra * 0.9;
            lbPrecioTotal.setText(String.format("$%.2f", totalConDescuento));
        });

        double finalTotalCompra1 = totalCompra;
        rbTransferencia.setOnAction(event -> {
            lbPrecioTotal.setText(String.format("$%.2f", finalTotalCompra1));
        });

        // Inicializar con el valor por defecto (Transferencia)
        lbPrecioTotal.setText(String.format("$%.2f", totalCompra));

        resumenCompra.append("\nTotal: $").append(String.format("%.2f", totalCompra));

        // Mostrar ventana con el resumen de la compra
        mostrarResumenCompra(grupoPago.getSelectedToggle() == rbEfectivo ? "Efectivo" : "Transferencia", resumenCompra.toString());
    }
    @FXML
    private void devolverArticulo(RegistroArticuloVentas articulo) {
        try {
            conectarABBDD(); // Asegurate de tener esta función
            CallableStatement stmt = conexionABBDD.prepareCall("CALL sp_devolver_articulo(?)");
            stmt.setInt(1, articulo.getIdArticulo());
            stmt.execute();
            mostrarAlerta("Informativo","Articulo devuelto correctamente.", Alert.AlertType.INFORMATION);
            refrescarTablaStock(); // Vuelve a cargar la tabla
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error","Error al devolver el artículo.: " + e.getMessage() , Alert.AlertType.ERROR);
        }
    }



    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarResumenCompra(String metodoPagoInicial, String resumen) {
        // Crear diálogo personalizado
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Resumen de Compra");

        // Crear los RadioButtons para el método de pago
        RadioButton rbEfectivo = new RadioButton("Efectivo");
        RadioButton rbTransferencia = new RadioButton("Transferencia");
        rbTransferencia.setSelected(true); // Transferencia por defecto

        ToggleGroup grupoPago = new ToggleGroup();
        rbEfectivo.setToggleGroup(grupoPago);
        rbTransferencia.setToggleGroup(grupoPago);

        // Etiqueta para el total
        Label lbTotal = new Label();

        // Calcular total inicial basado en el método de pago
        double totalCompra = calcularTotal(listaCompra, "Transferencia"); // Transferencia por defecto
        lbTotal.setText(String.format("Total: $%.2f", totalCompra));
        lbTotal.setStyle("-fx-font-weight: bold;");

        // Evento para actualizar el total si se cambia el método de pago
        rbEfectivo.setOnAction(event -> {
            double totalConDescuento = calcularTotal(listaCompra, "Efectivo");
            lbTotal.setText(String.format("Total: $%.2f", totalConDescuento));
        });

        rbTransferencia.setOnAction(event -> {
            lbTotal.setText(String.format("Total: $%.2f", calcularTotal(listaCompra, "Transferencia")));
        });

        // Crear un VBox con los elementos
        VBox contenido = new VBox(10,
                new Label("Seleccione método de pago:"),
                rbEfectivo, rbTransferencia,
                new Label("Resumen de la compra:"),
                new TextArea(resumen),
                lbTotal
        );
        contenido.setPadding(new Insets(10));

        // Agregar contenido al DialogPane
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        // Mostrar la ventana y procesar la acción del usuario
        dialogo.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String metodoPagoSeleccionado = rbEfectivo.isSelected() ? "Efectivo" : "Transferencia";
                double totalFinal = rbEfectivo.isSelected() ? calcularTotal(listaCompra, "Efectivo") : calcularTotal(listaCompra, "Transferencia");
                confirmarCompra(metodoPagoSeleccionado, totalFinal, listaCompra);
            }
        });
    }

    private double calcularTotal(List<RegistroArticuloVentas> listaCompra, String metodoPago) {
        double total = 0;
        for (RegistroArticuloVentas producto : listaCompra) {
            total += metodoPago.equals("Efectivo") ? producto.getPrecioEfectivo() : producto.getPrecioTransferencia();
        }
        return total;
    }

    private void confirmarCompra(String metodoPago, double totalCompra, ObservableList<RegistroArticuloVentas> listaCompra) {
        conectarABBDD();

        if (conexionABBDD != null) {
            try {
                conexionABBDD.setAutoCommit(false);

                Integer[] arrayIds = listaCompra.stream().map(RegistroArticuloVentas::getIdArticulo).toArray(Integer[]::new);
                Array sqlIdArray = conexionABBDD.createArrayOf("INTEGER", arrayIds);

                String sqlInsertarPago = "CALL public.sp_procesar_venta(?, ?, ?, ?)";
                try (PreparedStatement stmtPago = conexionABBDD.prepareStatement(sqlInsertarPago)) {
                    stmtPago.setDouble(1, totalCompra);
                    stmtPago.setString(2, nombreCliente);
                    stmtPago.setString(3, metodoPago);
                    stmtPago.setArray(4, sqlIdArray);
                    stmtPago.execute();
                }

                conexionABBDD.commit();
                mostrarAlerta("Compra confirmada", "La compra se ha registrado exitosamente.", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                // Revertir la transacción en caso de error
                try {
                    if (conexionABBDD != null) {
                        conexionABBDD.rollback();
                        mostrarAlerta("Error", "Se ha revertido la transacción debido a un error: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                } catch (SQLException rollbackEx) {
                    mostrarAlerta("Error Crítico", "No se pudo revertir la transacción: " + rollbackEx.getMessage(), Alert.AlertType.ERROR);
                }
            } finally {
                // Restaurar auto-commit y cerrar conexión
                try {
                    if (conexionABBDD != null) {
                        conexionABBDD.setAutoCommit(true);
                        desconexionABBDD();
                    }
                } catch (SQLException closeEx) {
                    mostrarAlerta("Error al Cerrar", "No se pudo restaurar auto-commit o cerrar la conexión: " + closeEx.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
        desconexionABBDD();
        refrescarTablaStock();
        limpiarTablaEstaCompra();

    }
    private void limpiarTablaEstaCompra(){
        tblArticuloEstaCompra.getItems().clear();
        actualizarResumen();
        lbPrecioTotal.setText("");
        btnFinalizarCompra.setDisable(true);
        btnCancelarCompra.setDisable(true);

    }

    //////////// FUNCIONES PESTAÑA CASHFLOW ////////////////////////
    private void configurarTablaCashFlow() {
        colFechaTransaccion.setCellValueFactory(new PropertyValueFactory<>("fechaTransaccion"));
        colDebe.setCellValueFactory(new PropertyValueFactory<>("debe"));
        colHaber.setCellValueFactory(new PropertyValueFactory<>("haber"));
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldo"));
        colObservacion.setCellValueFactory(new PropertyValueFactory<>("observacion"));
        btnConsultarCF.setDisable(true);
        dtFechaInicialCF.valueProperty().addListener((obs, oldValue, newValue) -> validarFechas());
        dtFechaFinalCF.valueProperty().addListener((obs, oldValue, newValue) -> validarFechas());
        // Vincular el botón con la ejecución de la consulta
        btnConsultarCF.setOnAction(event -> ejecutarConsulta());
        btnLimpiarCF.setOnAction(event -> limpiarCashflow());
    }

    private void validarFechas() {
        LocalDate fechaInicio = dtFechaFinalCF.getValue();
        LocalDate fechaFin = dtFechaFinalCF.getValue();

        // Habilita el botón solo si ambas fechas están seleccionadas
        btnConsultarCF.setDisable(fechaInicio == null && fechaFin == null);
    }

    @FXML
    private void ejecutarConsulta() {
        lstObsCashflow.clear();

        LocalDate fechaInicio = dtFechaInicialCF.getValue();
        LocalDate fechaFin = dtFechaFinalCF.getValue();

        // Validar intervalo de fechas
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta("Error", "Intervalo de fechas incorrecto", Alert.AlertType.ERROR);
            return;
        }

        String sql = "SELECT * FROM sp_analizar_cashflow(?, ?, ?)";

        conectarABBDD();

        try (PreparedStatement pstmt = conexionABBDD.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            pstmt.setInt(3, 1); // Opción 1

            try (ResultSet rs = pstmt.executeQuery()) {
                Double[] metrics = null; // Variable para almacenar las métricas

                while (rs.next()) {
                    String observacion = rs.getString("observacion");

                    // Normalizar la observación (elimina acentos y compara sin distinguir mayúsculas)
                    String observacionNormalizada = Normalizer.normalize(observacion.trim(), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "");

                    Array analisisArray = rs.getArray("analisis");

                    // Si es la fila de análisis, guardar las métricas pero NO agregarla a la tabla
                    if ("ANALISIS".equalsIgnoreCase(observacionNormalizada)) {
                        if (analisisArray != null) {
                            metrics = (Double[]) analisisArray.getArray();
                        }
                        continue; // Salta esta fila para que no aparezca en la tabla
                    }

                    // Agregar la fila normal a la tabla
                    RegistroTransaccionCF t = new RegistroTransaccionCF(
                            rs.getString("fecha_transaccion"),
                            rs.getDouble("debe"),
                            rs.getDouble("haber"),
                            rs.getDouble("saldo"),
                            observacion,
                            analisisArray
                    );
                    lstObsCashflow.add(t);
                }

                // Aplicar las métricas si se capturaron
                if (metrics != null) {
                    Double[] finalMetrics = metrics; // Para usar en runLater
                    Platform.runLater(() -> {
                        lbCantVentas.setText(String.valueOf(finalMetrics[0].intValue())); // Cantidad de ventas
                        lbTotalIngresos.setText(String.format("$%.2f", finalMetrics[1])); // Total ingresos
                        lbTotalGastos.setText(String.format("$%.2f", finalMetrics[2])); // Total gastos
                        lbSaldoTotal.setText(String.format("$%.2f", finalMetrics[3])); // Saldo total
                        lbCantGasto.setText(String.valueOf(finalMetrics[4].intValue())); // Cantidad de gastos
                        lbMayorIngreso.setText(String.format("$%.2f", finalMetrics[7])); // Mayor ingreso

                        // Convertir los timestamps a fecha legible
                        LocalDate diaMaxIngreso = Instant.ofEpochSecond(finalMetrics[8].longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate diaMaxGasto = Instant.ofEpochSecond(finalMetrics[9].longValue()).atZone(ZoneId.systemDefault()).toLocalDate();

                        lbDiaMayorIngreso.setText(diaMaxIngreso.toString()); // Día mayor ingreso
                        lbDiaMayorGasto.setText(diaMaxGasto.toString()); // Día mayor gasto
                    });
                }
            }
            tblCashFlow.setItems(lstObsCashflow);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        desconexionABBDD();
    }

    @FXML
    private void limpiarCashflow() {
        tblCashFlow.getItems().clear();
        lbCantVentas.setText("");
        lbTotalIngresos.setText("");
        lbTotalGastos.setText("");
        lbSaldoTotal.setText("");
        lbMayorIngreso.setText("");
        lbCantGasto.setText("");
        lbDiaMayorIngreso.setText("");
        lbDiaMayorGasto.setText("");
        dtFechaInicialCF.setValue(null);
        dtFechaFinalCF.setValue(null);
        btnConsultarCF.setDisable(true);
    }

    public void conectarABBDD() {
        String url = "jdbc:postgresql://autorack.proxy.rlwy.net:17870/railway";
        String user = "postgres"; // O el usuario que aparece en Railway
        String password = "CYAFNVxEfUHpElpvEDPdVkMVecpWTNKq"; // Copia la contraseña de Railway

        conexionABBDD = null;
        try {
            conexionABBDD = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión exitosa a Railway PostgreSQL.");
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar con PostgreSQL en Railway.");
            e.printStackTrace();
        }
    }

    /* String url = "jdbc:postgresql://localhost:5432/bd_CapriRopa";
    String user = "postgres";
    String password = "GloMinte@89";*/

/*    String url = "jdbc:postgresql://autorack.proxy.rlwy.net:17870/railway";
    String user = "postgres"; // O el usuario que aparece en Railway
    String password = "CYAFNVxEfUHpElpvEDPdVkMVecpWTNKq"; // Copia la contraseña de Railway*/
    public void desconexionABBDD(){
        if(conexionABBDD!=null){
            try{
                conexionABBDD.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

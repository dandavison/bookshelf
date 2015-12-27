var TableRow = React.createClass({
  render: function() {
    return (
      <tr>
        <td>{this.props.row.name}</td>
        <td>{this.props.row.size}</td>
      </tr>
    )
  }
})

var Table = React.createClass({
  render: function() {
    var rows = [];
    this.state.rows.forEach(function(row){
      rows.push(<TableRow key={row.hash} row={row} />)
    })
    return (
      <table>
        <thead>
          <tr>
            <td>Name</td>
            <td>Size</td>
          </tr>
        </thead>
        <tbody>
          {rows}
        </tbody>
      </table>
    )
  },
  getInitialState: function() {
    return {
      rows: [],
      message: ''
    }
  },
  componentDidMount: function() {
    this.fetchData();
  },
  fetchData: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      // cache: false,
      success: function(data) {
        this.setState({
          rows: data
        });
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
        this.setState({
          message: err.toString()
        });
      }.bind(this)
    })
  }
})

ReactDOM.render(
  <Table rows={[]} url='/json/' />,
  document.getElementById('content')
);
